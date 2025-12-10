package io.zeitwert.fm.oe.persist.jooq

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.ReferenceSetProperty
import io.zeitwert.dddrive.ddd.persist.jooq.JooqObjPersistenceProviderBase
import io.zeitwert.fm.obj.model.db.Sequences
import io.zeitwert.fm.oe.model.ObjUserFM
import io.zeitwert.fm.oe.model.ObjUserFMRepository
import io.zeitwert.fm.oe.model.db.Tables
import io.zeitwert.fm.oe.model.enums.CodeUserRole
import org.jooq.DSLContext
import org.jooq.UpdatableRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

/**
 * jOOQ-based persistence provider for ObjUserFM aggregates.
 */
@Component("objUserFMPersistenceProvider")
open class ObjUserFMPersistenceProvider : JooqObjPersistenceProviderBase<ObjUserFM>() {

    private lateinit var _dslContext: DSLContext
    private lateinit var _repository: ObjUserFMRepository

    @Autowired
    fun setDslContext(dslContext: DSLContext) {
        this._dslContext = dslContext
    }

    @Autowired
    @Lazy
    fun setRepository(repository: ObjUserFMRepository) {
        this._repository = repository
    }

    override fun dslContext(): DSLContext = _dslContext

    override fun getRepository(): ObjUserFMRepository = _repository

    override fun getAggregateTypeId(): String = AGGREGATE_TYPE_ID

    override fun nextAggregateId(): Any {
        return dslContext()
            .nextval(Sequences.OBJ_ID_SEQ)
            .toInt()
    }

    override fun fromAggregate(aggregate: ObjUserFM): UpdatableRecord<*> {
        return createObjRecord(aggregate)
    }

    @Suppress("UNCHECKED_CAST")
    override fun storeExtension(aggregate: ObjUserFM) {
        val objId = aggregate.id as Int

        val existingRecord = dslContext().fetchOne(
            Tables.OBJ_USER,
            Tables.OBJ_USER.OBJ_ID.eq(objId)
        )

        val record = existingRecord ?: dslContext().newRecord(Tables.OBJ_USER)

        record.objId = objId
        record.tenantId = aggregate.tenantId as? Int
        record.email = (aggregate.getProperty("email") as? BaseProperty<String?>)?.value
        record.name = (aggregate.getProperty("name") as? BaseProperty<String?>)?.value
        record.description = (aggregate.getProperty("description") as? BaseProperty<String?>)?.value
        record.roleList = (aggregate.getProperty("role") as? EnumProperty<CodeUserRole>)?.value?.id
        record.password = (aggregate.getProperty("password") as? BaseProperty<String?>)?.value
        record.needPasswordChange = (aggregate.getProperty("needPasswordChange") as? BaseProperty<Boolean?>)?.value
        // TODO-MIGRATION: DMS - uncomment after DMS is migrated
        // record.avatarImgId = (aggregate.getProperty("avatarImage") as? ReferenceProperty<*>)?.id as? Int

        if (existingRecord != null) {
            record.update()
        } else {
            record.insert()
        }

        // Store tenant set (many-to-many relationship)
        storeTenantSet(aggregate, objId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun storeTenantSet(aggregate: ObjUserFM, objId: Int) {
        val tenantSet = (aggregate.getProperty("tenantSet") as? ReferenceSetProperty<*>)?.getItems() ?: return

        // Delete existing mappings
        dslContext().deleteFrom(Tables.OBJ_USER)
            .where(Tables.OBJ_USER.OBJ_ID.eq(objId))

        // Note: tenant set storage may need a separate mapping table
        // This is a simplified implementation - adjust based on actual schema
    }

    @Suppress("UNCHECKED_CAST")
    override fun loadExtension(aggregate: ObjUserFM, objId: Int?) {
        if (objId == null) return

        val record = dslContext().fetchOne(
            Tables.OBJ_USER,
            Tables.OBJ_USER.OBJ_ID.eq(objId)
        ) ?: return

        (aggregate.getProperty("email") as? BaseProperty<String?>)?.value = record.email
        (aggregate.getProperty("name") as? BaseProperty<String?>)?.value = record.name
        (aggregate.getProperty("description") as? BaseProperty<String?>)?.value = record.description

        record.roleList?.let { roleId ->
            (aggregate.getProperty("role") as? EnumProperty<CodeUserRole>)?.value =
                CodeUserRole.getUserRole(roleId)
        }

        (aggregate.getProperty("password") as? BaseProperty<String?>)?.value = record.password
        (aggregate.getProperty("needPasswordChange") as? BaseProperty<Boolean?>)?.value = record.needPasswordChange

        // TODO-MIGRATION: DMS - uncomment after DMS is migrated
        // (aggregate.getProperty("avatarImage") as? ReferenceProperty<*>)?.id = record.avatarImgId

        // Load tenant set (many-to-many relationship)
        loadTenantSet(aggregate, objId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadTenantSet(aggregate: ObjUserFM, objId: Int) {
        // Note: tenant set loading may need a separate mapping table
        // This is a simplified implementation - adjust based on actual schema
    }

    companion object {
        private const val AGGREGATE_TYPE_ID = "obj_user"
    }
}

