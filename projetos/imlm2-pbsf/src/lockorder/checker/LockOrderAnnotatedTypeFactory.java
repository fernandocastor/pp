package lockorder.checker;

import javax.lang.model.element.AnnotationMirror;

import lockorder.checker.quals.LockFree;
import lockorder.checker.quals.LockOrder;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.ListTreeAnnotator;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.TreeAnnotator;
import org.checkerframework.framework.util.GraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationUtils;

public class LockOrderAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    private final AnnotationMirror LOCKORDER, LOCKFREE;

    public LockOrderAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        LOCKORDER = AnnotationUtils.fromClass(elements, LockOrder.class);
        LOCKFREE = AnnotationUtils.fromClass(elements, LockFree.class);
        postInit();
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(MultiGraphFactory factory) {
        return new LockOrderQualifierHierarchy(factory, LOCKORDER);
    }

    private class LockOrderQualifierHierarchy extends GraphQualifierHierarchy {

        public LockOrderQualifierHierarchy(MultiGraphFactory f,
                AnnotationMirror bottom) {
            super(f, bottom);
        }

        @Override
        public boolean isSubtype(AnnotationMirror rhs, AnnotationMirror lhs) {
            if (AnnotationUtils.areSameIgnoringValues(rhs, LOCKORDER) && 
                    AnnotationUtils.areSameIgnoringValues(lhs, LOCKORDER)) {
                //If both are @LockOrder, they need to be exactly the same.
                return AnnotationUtils.areSame(rhs,lhs);
            } else if (AnnotationUtils.areSameIgnoringValues(rhs, LOCKFREE)
                    || AnnotationUtils.areSameIgnoringValues(lhs, LOCKFREE)) {
                //Here one of them is not @LockOrder, so it is ok.
                return true;
            }
            return super.isSubtype(rhs, lhs);
        }

        @Override
        public AnnotationMirror getTopAnnotation(AnnotationMirror start) {
            if (AnnotationUtils.areSameByClass(start, LockOrder.class) ||
                    AnnotationUtils.areSameByClass(start, LockFree.class)) {
                return LOCKFREE;
            }
            checker.errorAbort("Unexpected AnnotationMirror: " + start);
            return null; // dead code
        }

        @Override
        public AnnotationMirror getBottomAnnotation(AnnotationMirror start) {
            if (AnnotationUtils.areSameByClass(start, LockOrder.class) ||
                    AnnotationUtils.areSameByClass(start, LockFree.class)) {
                return LOCKORDER;
            }
            checker.errorAbort("Unexpected AnnotationMirror: " + start);
            return null; // dead code
        }

    }
//
    @Override
    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        CFTransfer ret = new LockOrderTransfer(analysis);
        return ret;
    }
//
//    @Override
//    public TreeAnnotator createTreeAnnotator() {
//        return new ListTreeAnnotator(
//                super.createTreeAnnotator(),
//                new LockOrderTreeAnnotator(this)
//        );
//    }
//
//    private class LockOrderTreeAnnotator extends TreeAnnotator {
//        public LockOrderTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
//            super(atypeFactory);
//        }
//    }
}
