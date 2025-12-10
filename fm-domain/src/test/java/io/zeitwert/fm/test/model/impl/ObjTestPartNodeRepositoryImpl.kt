package io.zeitwert.fm.test.model.impl

import io.dddrive.core.ddd.model.base.PartRepositoryBase
import io.zeitwert.fm.test.model.ObjTest
import io.zeitwert.fm.test.model.ObjTestPartNode
import io.zeitwert.fm.test.model.ObjTestPartNodeRepository
import io.zeitwert.fm.test.model.base.ObjTestPartNodeBase
import org.springframework.stereotype.Component

/**
 * Repository implementation for ObjTestPartNode using the NEW dddrive framework.
 */
@Component("testPartNodeRepository")
class ObjTestPartNodeRepositoryImpl : PartRepositoryBase<ObjTest, ObjTestPartNode>(
    ObjTest::class.java,
    ObjTestPartNode::class.java,
    ObjTestPartNodeBase::class.java
), ObjTestPartNodeRepository

