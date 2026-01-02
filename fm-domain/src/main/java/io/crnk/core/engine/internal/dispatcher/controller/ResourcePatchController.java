package io.crnk.core.engine.internal.dispatcher.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.crnk.core.engine.dispatcher.Response;
import io.crnk.core.engine.document.Document;
import io.crnk.core.engine.document.Resource;
import io.crnk.core.engine.http.HttpMethod;
import io.crnk.core.engine.information.resource.ResourceField;
import io.crnk.core.engine.information.resource.ResourceInformation;
import io.crnk.core.engine.internal.dispatcher.path.JsonPath;
import io.crnk.core.engine.internal.dispatcher.path.ResourcePath;
import io.crnk.core.engine.internal.document.mapper.DocumentMapper;
import io.crnk.core.engine.internal.document.mapper.DocumentMappingConfig;
import io.crnk.core.engine.internal.repository.ResourceRepositoryAdapter;
import io.crnk.core.engine.internal.utils.ClassUtils;
import io.crnk.core.engine.internal.utils.ExceptionUtil;
import io.crnk.core.engine.internal.utils.PreconditionUtil;
import io.crnk.core.engine.query.QueryAdapter;
import io.crnk.core.engine.query.QueryContext;
import io.crnk.core.engine.registry.RegistryEntry;
import io.crnk.core.engine.result.Result;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.repository.response.JsonApiResponse;
import io.crnk.core.resource.annotations.PatchStrategy;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;

public class ResourcePatchController extends ResourceUpsert {

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PATCH;
	}

	@Override
	public boolean isAcceptable(JsonPath jsonPath, String method) {
		return !jsonPath.isCollection() && jsonPath instanceof ResourcePath && HttpMethod.PATCH.name().equals(method);
	}

	@Override
	public Result<Response> handleAsync(JsonPath jsonPath, QueryAdapter queryAdapter, Document requestDocument) {

		RegistryEntry endpointRegistryEntry = jsonPath.getRootEntry();
		final Resource requestResource = this.getRequestBody(requestDocument, jsonPath, HttpMethod.PATCH);
		RegistryEntry registryEntry = this.context.getResourceRegistry().getEntry(requestResource.getType());
		this.logger.debug("using registry entry {}", registryEntry);

		Serializable resourceId = jsonPath.getId();

		ResourceInformation resourceInformation = registryEntry.getResourceInformation();
		this.verifyTypes(HttpMethod.PATCH, endpointRegistryEntry, registryEntry);
		DocumentMappingConfig mappingConfig = this.context.getMappingConfig();
		DocumentMapper documentMapper = this.context.getDocumentMapper();
		QueryContext queryContext = queryAdapter.getQueryContext();

		ResourceRepositoryAdapter resourceRepository = endpointRegistryEntry.getResourceRepository();
		return resourceRepository.findOne(resourceId, queryAdapter).merge(existingResponse -> {
			Object existingEntity = existingResponse.getEntity();
			this.checkNotNull(existingEntity, jsonPath);
			resourceInformation.verify(existingEntity, requestDocument);
			return documentMapper.toDocument(existingResponse, queryAdapter, mappingConfig).map(it -> it.getSingleData().get()).doWork(existing -> this.mergeNestedAttribute(existing, requestResource, queryContext, resourceInformation)).map(it -> existingEntity);
		}).merge(existingEntity -> this.applyChanges(registryEntry, existingEntity, requestResource, queryAdapter)).map(this::toResponse);
	}

	private Response toResponse(Document updatedDocument) {
		if (!updatedDocument.getData().isPresent() && (updatedDocument.getErrors() == null || updatedDocument.getErrors().isEmpty())) {
			updatedDocument = null;
		}
		int status = this.getStatus(updatedDocument, HttpMethod.PATCH);
		return new Response(updatedDocument, status);
	}

	private Result<Document> applyChanges(RegistryEntry registryEntry, Object entity, Resource requestResource, QueryAdapter queryAdapter) {
		ResourceInformation resourceInformation = registryEntry.getResourceInformation();
		ResourceRepositoryAdapter resourceRepository = registryEntry.getResourceRepository();

		Set<String> loadedRelationshipNames;
		Result<JsonApiResponse> updatedResource;
		if (resourceInformation.getImplementationClass() == Resource.class) {
			loadedRelationshipNames = this.getLoadedRelationshipNames(requestResource);
			updatedResource = resourceRepository.update(requestResource, queryAdapter);
		} else {
			QueryContext queryContext = queryAdapter.getQueryContext();
			this.setAttributes(requestResource, entity, resourceInformation, queryContext);
			this.setMeta(requestResource, entity, resourceInformation);
			this.setLinks(requestResource, entity, resourceInformation);

			loadedRelationshipNames = this.getLoadedRelationshipNames(requestResource);

			Result<List> relationsResult = this.setRelationsAsync(entity, registryEntry, requestResource, queryAdapter, false);
			updatedResource = relationsResult.merge(it -> resourceRepository.update(entity, queryAdapter));
		}

		DocumentMappingConfig mappingConfig = this.context.getMappingConfig().clone();
		mappingConfig.setFieldsWithEnforcedIdSerialization(loadedRelationshipNames);
		DocumentMapper documentMapper = this.context.getDocumentMapper();

		return updatedResource.doWork(it -> this.logger.debug("patched resource {}", it)).merge(it -> documentMapper.toDocument(it, queryAdapter, mappingConfig));
	}

	private void mergeNestedAttribute(Resource existingResource, Resource requestResource, QueryContext queryContext, ResourceInformation resourceInformation) {
		// extract current attributes from findOne without any manipulation by query
		// params (such as sparse fieldsets)
		ExceptionUtil.wrapCatchedExceptions(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ObjectMapper objectMapper = ResourcePatchController.this.context.getObjectMapper();

				String attributesFromFindOne = ResourcePatchController.this.extractAttributesFromResourceAsJson(existingResource);
				Map<String, Object> attributesToUpdate = new HashMap<>(ResourcePatchController.this.emptyIfNull(objectMapper.readValue(attributesFromFindOne, Map.class)));

				// deserialize the request JSON's attributes object into a map
				String attributesAsJson = objectMapper.writeValueAsString(requestResource.getAttributes());
				Map<String, Object> attributesFromRequest = ResourcePatchController.this.emptyIfNull(objectMapper.readValue(attributesAsJson, Map.class));

				// remove attributes that were omitted in the request
				Iterator<String> it = attributesToUpdate.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (!attributesFromRequest.containsKey(key)) {
						it.remove();
					}
				}

				// walk the source map and apply target values from request
				ResourcePatchController.this.updateValues(attributesToUpdate, attributesFromRequest, resourceInformation, queryContext, null);
				Map<String, JsonNode> upsertedAttributes = new HashMap<>();
				for (Map.Entry<String, Object> entry : attributesToUpdate.entrySet()) {
					JsonNode value = objectMapper.valueToTree(entry.getValue());
					upsertedAttributes.put(entry.getKey(), value);
				}

				requestResource.setAttributes(upsertedAttributes);
				return null;
			}
		}, "failed to merge patched attributes");
	}

	private void checkNotNull(Object resource, JsonPath jsonPath) {
		if (resource == null) {
			throw new ResourceNotFoundException(jsonPath.toString());
		}
	}

	private <K, V> Map<K, V> emptyIfNull(Map<K, V> value) {
		return value != null ? value : Collections.emptyMap();
	}

	private String extractAttributesFromResourceAsJson(Resource resource) throws IOException {

		JsonApiResponse response = new JsonApiResponse();
		response.setEntity(resource);
		// deserialize using the objectMapper so it becomes json-api
		ObjectMapper objectMapper = this.context.getObjectMapper();
		String newRequestBody = objectMapper.writeValueAsString(resource);
		JsonNode node = objectMapper.readTree(newRequestBody);
		JsonNode attributes = node.findValue("attributes");
		return objectMapper.writeValueAsString(attributes);

	}

	private void updateValues(Map<String, Object> source, Map<String, Object> updates, ResourceInformation resourceInformation, QueryContext queryContext, PatchStrategy patchStrategy) {

		int requestVersion = queryContext.getRequestVersion();
		for (Map.Entry<String, Object> entry : updates.entrySet()) {
			String fieldName = entry.getKey();
			Object updatedValue = entry.getValue();

			// updating an embedded object
			if (updatedValue instanceof Map) {

				// we don't have yet ResourceFields for nested resource, make use of root
				// patchStrategy instead
				if (patchStrategy == null) {
					ResourceField field = resourceInformation.findFieldByJsonName(fieldName, requestVersion);
					if (field != null) {
						patchStrategy = field.getPatchStrategy();
					} else if (ClassUtils.isGenericResource(resourceInformation.getResourceClass())) {
						// GenericResourceBase uses dynamic attributes - use SET strategy
						patchStrategy = PatchStrategy.SET;
					} else {
						PreconditionUtil.verify(false, "field %s not found", fieldName);
					}
				}

				// source may lack the whole entry yet
				if (source.get(fieldName) == null) {
					source.put(fieldName, new HashMap<>());
				}

				if (patchStrategy == PatchStrategy.SET) {
					source.put(fieldName, updatedValue);
				} else {
					Object sourceMap = source.get(fieldName);
					this.updateValues((Map<String, Object>) sourceMap, (Map<String, Object>) updatedValue, resourceInformation, queryContext, patchStrategy);
				}
				continue;
			}

			// updating a simple value
			source.put(fieldName, updatedValue);
		}
	}

}
