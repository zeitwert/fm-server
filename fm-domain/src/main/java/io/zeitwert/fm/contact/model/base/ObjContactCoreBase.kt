package io.zeitwert.fm.contact.model.base

import io.dddrive.core.property.model.BaseProperty
import io.dddrive.core.property.model.EnumProperty
import io.dddrive.core.property.model.PartListProperty
import io.dddrive.core.property.model.ReferenceProperty
import io.zeitwert.fm.account.model.ObjAccount
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin
import io.zeitwert.fm.contact.model.ObjContact
import io.zeitwert.fm.contact.model.ObjContactPartAddress
import io.zeitwert.fm.contact.model.ObjContactRepository
import io.zeitwert.fm.contact.model.enums.CodeContactRole
import io.zeitwert.fm.contact.model.enums.CodeSalutation
import io.zeitwert.fm.contact.model.enums.CodeTitle
import io.zeitwert.fm.obj.model.base.FMObjCoreBase
import java.time.LocalDate

/**
 * Base class for ObjContact using the NEW dddrive framework.
 */
abstract class ObjContactCoreBase(
    repository: ObjContactRepository
) : FMObjCoreBase(repository), ObjContact, AggregateWithNotesMixin {

    override fun aggregate(): ObjContact = this

    //@formatter:off
    private val _contactRole: EnumProperty<CodeContactRole> = this.addEnumProperty("contactRole", CodeContactRole::class.java)
    private val _salutation: EnumProperty<CodeSalutation> = this.addEnumProperty("salutation", CodeSalutation::class.java)
    private val _title: EnumProperty<CodeTitle> = this.addEnumProperty("title", CodeTitle::class.java)
    private val _firstName: BaseProperty<String> = this.addBaseProperty("firstName", String::class.java)
    private val _lastName: BaseProperty<String> = this.addBaseProperty("lastName", String::class.java)
    private val _birthDate: BaseProperty<LocalDate> = this.addBaseProperty("birthDate", LocalDate::class.java)
    private val _phone: BaseProperty<String> = this.addBaseProperty("phone", String::class.java)
    private val _mobile: BaseProperty<String> = this.addBaseProperty("mobile", String::class.java)
    private val _email: BaseProperty<String> = this.addBaseProperty("email", String::class.java)
    private val _description: BaseProperty<String> = this.addBaseProperty("description", String::class.java)
    private val _addressList: PartListProperty<ObjContactPartAddress> = this.addPartListProperty("addressList", ObjContactPartAddress::class.java)
    
    private val _account: ReferenceProperty<ObjAccount> = this.addReferenceProperty("account", ObjAccount::class.java)
    //@formatter:on

    override fun getRepository(): ObjContactRepository {
        return super.getRepository() as ObjContactRepository
    }

    override fun doCalcAll() {
        super.doCalcAll()
        calcCaption()
        doCalcSearch()
    }

    private fun calcCaption() {
        this.caption.value = "${getFirstName() ?: ""} ${getLastName() ?: ""}".trim()
    }

     private fun doCalcSearch() {
    //     addSearchToken(getFirstName())
    //     addSearchToken(getLastName())
    //     addSearchToken(getEmail())
    //     getEmail()?.let { email ->
    //         addSearchText(email.replace("@", " ").replace(".", " ").replace("_", " ").replace("-", " "))
    //     }
    //     addSearchText(getDescription())
     }

    // ObjContact interface implementation

    override fun getContactRole(): CodeContactRole? = _contactRole.value

    override fun setContactRole(contactRole: CodeContactRole?) {
        _contactRole.value = contactRole
    }

    override fun getSalutation(): CodeSalutation? = _salutation.value

    override fun setSalutation(salutation: CodeSalutation?) {
        _salutation.value = salutation
    }

    override fun getTitle(): CodeTitle? = _title.value

    override fun setTitle(title: CodeTitle?) {
        _title.value = title
    }

    override fun getFirstName(): String? = _firstName.value

    override fun setFirstName(firstName: String?) {
        _firstName.value = firstName
    }

    override fun getLastName(): String? = _lastName.value

    override fun setLastName(lastName: String?) {
        _lastName.value = lastName
    }

    override fun getBirthDate(): LocalDate? = _birthDate.value

    override fun setBirthDate(birthDate: LocalDate?) {
        _birthDate.value = birthDate
    }

    override fun getPhone(): String? = _phone.value

    override fun setPhone(phone: String?) {
        _phone.value = phone
    }

    override fun getMobile(): String? = _mobile.value

    override fun setMobile(mobile: String?) {
        _mobile.value = mobile
    }

    override fun getEmail(): String? = _email.value

    override fun setEmail(email: String?) {
        _email.value = email
    }

    override fun getDescription(): String? = _description.value

    override fun setDescription(description: String?) {
        _description.value = description
    }

    // ItemWithAccount implementation

    override fun getAccount(): ObjAccount? {
        return _account.value
    }

    // Address list implementation

    override fun getMailAddressList(): List<ObjContactPartAddress> {
        return _addressList.parts.filter { it.isMailAddress == true }
    }

    override fun getMailAddress(addressId: Int?): java.util.Optional<ObjContactPartAddress> {
        return java.util.Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == true })
    }

    override fun clearMailAddressList() {
        getMailAddressList().forEach { _addressList.removePart(it.id) }
    }

    override fun addMailAddress(): ObjContactPartAddress {
        return _addressList.addPart(null)
    }

    override fun removeMailAddress(addressId: Int?) {
        _addressList.removePart(addressId)
    }

    override fun getElectronicAddressList(): List<ObjContactPartAddress> {
        return _addressList.parts.filter { it.isMailAddress == false }
    }

    override fun getElectronicAddress(addressId: Int?): java.util.Optional<ObjContactPartAddress> {
        return java.util.Optional.ofNullable(_addressList.parts.find { it.id == addressId && it.isMailAddress == false })
    }

    override fun clearElectronicAddressList() {
        getElectronicAddressList().forEach { _addressList.removePart(it.id) }
    }

    override fun addElectronicAddress(): ObjContactPartAddress {
        return _addressList.addPart(null)
    }

    override fun removeElectronicAddress(addressId: Int?) {
        _addressList.removePart(addressId)
    }
}

