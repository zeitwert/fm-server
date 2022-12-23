
common
	size
	label
	value
	isVisible
	isRequired
	isReadonly
	isEnabled
	isIgnored ??
	styleClass ??

text

textarea

date, dateTime
	enableTime: true | false
	format: "dddd, DD.MM.YYYY HH:mm"
	minDate: "today"
	maxDate: "today"

boolean

number
	numberFormat: ".,",
	fractionSize: 0,
	defaultValue: 15,

richText

select enum
	multi !!!
	dataSource: Rest | Static
	storage: Full | Id??
	identity: id
	enableAutocomplete: false
	queryUrl: {{enumBaseUrl}}/[module]/[codeTable]
	queryUrl: {{enumBaseUrl}}/dms/codeDocumentStatus{{document.documentCategory.documentLifecycle.id ? '/' + document.documentCategory.documentLifecycle.id : '' }}
	formatItem: {{$item.name}}

select object
	dataSource: Rest
	storage: Id
	lookupUrl: {{apiBaseUrl}}/[module]/[type]/{{$id}}
	identity: id
	formatItem: {{$item.name}}
	queryUrl: {{apiBaseUrl}}/doc/docs?filter[account.id]={{advice.account}}&filter[docTypeId][IN]=doc_advice,doc_opportunity,doc_lead
	queryUrl: {{apiBaseUrl}}/account/accounts?filter[searchText]={{$searchText}}
	enableAutocomplete: true | false
	autocompleteMinLength: 2



accordion

panel

subform

hr

upload

dataTable
	link
	htmlComponent

scriptButton
