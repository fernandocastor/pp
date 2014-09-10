package lockorder.checker;

import lockorder.checker.quals.LockOrder;

import org.checkerframework.dataflow.analysis.RegularTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;

public class LockOrderTransfer extends CFTransfer {

    final LockOrderAnnotatedTypeFactory factory;
    public LockOrderTransfer(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        super(analysis);
        factory = (LockOrderAnnotatedTypeFactory) analysis.getTypeFactory();
    }

    @Override
    public TransferResult<CFValue, CFStore> visitAssignment(AssignmentNode n,
            TransferInput<CFValue, CFStore> in) {
        // Do not refine assignments where @LockFree is on the RHS.
        if (!factory.getAnnotatedType(n.getExpression().getTree()).
                hasAnnotation(LockOrder.class)) {
            return new RegularTransferResult<>(null, in.getRegularStore());
        }
        return super.visitAssignment(n, in);
    }

}