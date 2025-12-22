
# Framework Refactoring

## Obj, Doc

* dropped generic ref_obj_id, ref_doc_id
* added closed_at, closed_by_user_id
* dropped last_viewed

## Doc

* is_in_work nullable

## Parts

* standard part structure (xyz_id, parent_part_id, part_list_type_id, seq_nr)



# Todo

* xyz_part_transition: modified_at => created_at?
* constraint xyz => index unique?
* intl_key, extl_key => key?

