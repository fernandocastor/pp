package lockorder.checker;

import lockorder.checker.quals.LockFree;
import lockorder.checker.quals.LockOrder;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.StubFiles;
import org.checkerframework.framework.qual.TypeQualifiers;

/**
 * This checker allows the developer to assign an order between all the
 * locks used in a java program.
 */

@TypeQualifiers({LockOrder.class, LockFree.class})
@StubFiles("lock.astub")

public class LockOrderChecker extends BaseTypeChecker {

}
