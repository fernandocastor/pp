package tests;

import lockorder.checker.quals.*;

/**
 * For an assignment where the LHS has type @LockOrder, the RHS must have
 * exact same type, or @LockFree. Everything is @LockFree by default.
 * See method m1() below for examples.
 * 
 *
 * If the RHS is @LockOrder and the LHS is @LockFree, the LHS will have its type
 * refined. When the RHS is a subtype of the LHS, the LHS refines its type to that 
 * of the RHS. This only works for local variables. It is not sound to assume
 * type refinement on class fields for example.
 * FOR SOME REASON, DATAFLOW REFINEMENT IS NOT WORKING AS INTENTED
 * See method m2() below for examples.
 * 
 * 
 
 * @author pbsf and imlm2
 *
 */
public class Assignments {

    // Suppressing cycle detection warnings for local variables
    @SuppressWarnings("local.cycle.detection.warning")
    void m1() {
        //Assignments below are allowed because RHS is @LockFree.
        @LockOrder(before={}) Object a = new Object();
        @LockOrder(before={"a"}) Object c = new Object();
        @LockOrder(before={"a", "c"}) Object b = new Object();
        // Order to obtain locks: b -> c -> a
        
        //Assignments below are allowed because types are the same.
        @LockOrder(before={}) Object refA = a;
        @LockOrder(before={"a", "c"}) Object refB = b;
        @LockOrder(before={"a"}) Object refC = c;

        //Assignments below are not allowed because LHS is @LockOrder, RHS too,
        //but they are not the same.
        //::error: (assignment.type.incompatible)
        @LockOrder(before={"d","e"}) Object notAllowed1 = a;
        //::error: (assignment.type.incompatible)
        @LockOrder(before={"d","e"}) Object notAllowed2 = b;
        //::error: (assignment.type.incompatible)
        @LockOrder(before={"d","e"}) Object notAllowed3 = c;

    }
}