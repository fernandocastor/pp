package lockorder.checker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import lockorder.checker.quals.LockFree;
import lockorder.checker.quals.LockMethod;
import lockorder.checker.quals.LockOrder;
import lockorder.checker.quals.UnlockMethod;

import org.checkerframework.checker.lock.qual.Holding;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.util.Resolver2;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.InternalUtils;
import org.checkerframework.javacutil.TreeUtils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Pair;

public class LockOrderVisitor extends BaseTypeVisitor<LockOrderAnnotatedTypeFactory> {

    List<String> currentLocksHeld = new ArrayList<String>();

    public LockOrderVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        currentLocksHeld.clear();
        Element ele = InternalUtils.symbol(node);
        AnnotationMirror holdAnno = atypeFactory.getDeclAnnotation(ele, Holding.class);
        if(holdAnno != null) {
            List<String> locks = AnnotationUtils.getElementValueArray(holdAnno,
                    "value", String.class, true);
            currentLocksHeld.addAll(locks);
        }
        return super.visitMethod(node, p);
    }

    @Override
    public Void visitSynchronized(SynchronizedTree node, Void p) {
        Pair<String, ExpressionTree> syncNode = getSyncObject(node);
        checkLock(syncNode.snd, syncNode.fst);
        Void result = super.visitSynchronized(node, p);
        //After exiting sychronized block, remove it from the list.
        unlock(syncNode.fst);
        return result;
    }

    @Override
    protected void checkMethodInvocability(AnnotatedExecutableType method,
            MethodInvocationTree node) {
        Element ele = InternalUtils.symbol(node);
        if (atypeFactory.getDeclAnnotation(ele, LockMethod.class) != null) {
            //Is lock() method.
            Pair<String, ExpressionTree> syncNode = getSyncObject(node);
            checkLock(syncNode.snd, syncNode.fst);
            return;
        } else if (atypeFactory.getDeclAnnotation(ele,
                UnlockMethod.class) != null) {
            Pair<String, ExpressionTree> syncNode = getSyncObject(node);
            unlock(syncNode.fst);
            return;
        }
        super.checkMethodInvocability(method, node);
    }

    @Override
    public Void visitAnnotation(AnnotationTree node, Void p) {
        Element anno = TreeInfo.symbol((JCTree) node.getAnnotationType());
        if (anno.toString().equals(LockFree.class.getName())) {
            checker.report(Result.failure("annotation.not.allowed",
                    anno.toString()), node);
            return null;
        }
        return super.visitAnnotation(node, p);
    }

    private Pair<String, ExpressionTree> getSyncObject(Tree node) {
        ExpressionTree expTree = null;
        String name = null;
        if (node instanceof SynchronizedTree) {
            expTree = ((SynchronizedTree) node).getExpression();
            String exp = expTree.toString();
            name = exp.substring(1,exp.toString().length()-1);
        } else if (node instanceof MethodInvocationTree) {
            expTree = TreeUtils.getReceiverTree((MethodInvocationTree) node);
            name = expTree.toString();
        } else {
            throw new IllegalArgumentException();
        }
        return new Pair<String, ExpressionTree>(name, expTree);
    }

    private void checkLock(ExpressionTree node, String name) {
        TypeMirror elt = TreeUtils.elementFromUse(node).asType();
        List<? extends AnnotationMirror> annos = elt.getAnnotationMirrors();
        List<String> before = null;
        for (AnnotationMirror am : annos) {
            if (AnnotationUtils.areSameByClass(am, LockOrder.class)) {
                before = AnnotationUtils.getElementValueArray(am, "before",
                        String.class, true);
            }
        }
        
        if (before != null && !before.isEmpty()) {
            for (String s : before) {
                
                if (/* checkCycles(getCurrentFlowExprContext(), name, new HashSet<>(), node) && */
                        currentLocksHeld.contains(s)) {
                    checker.report(Result.failure("wrong.lock.order", name, s),
                            node);
                    //return;
                }
            }
        }
        //Add current synchronized object to list.
        currentLocksHeld.add(name);
    }

    private void unlock(String name) {
        currentLocksHeld.remove(name);
    }

    /*
     * This check applies to DEFINITIONS of @LockOrder types and will inform the error on the
     * type declaration sites.
     * This gets called in the visitVariable(..) method.
     * 
     * The rationale here is:
     * (1) For each variable declaration var_decl annotated with @LockOrder that has a non-empty
     * list of lock names:
     *      (1.1) For each lock name lock_name in var_decl's @LockOrder list and using var_decl's current context (scope):
     *          (1.1.1) If lock_name is in the visited names list, inform error and abort, else:
     *          (1.1.2) Add lock_name to the visited names list
     *          (1.1.3) Look for the *declaration* of the variable lock_name and its (declaration) context
     *          (1.1.4) Start the search from (1) now using the resolved declaration and context from (1.1.3)
     */
    // Node <-> Tree <-> Element <-> TreePath
    @Override
    public Void visitVariable(VariableTree node, Void p) {
        Element var = InternalUtils.symbol(node);
        LockOrder anno = var.getAnnotation(LockOrder.class);
        if (anno != null) {
            // Special casing for local variable declarations that make use of the
            // @LockOrder annotation, unfortunately, at the moment we are unable to
            // check for lock order cycles as we are incapable of getting a valid
            // TreePath context for such declarations.
            // TODO: Verify correctness for parameters and type parameters
            if(var.getKind() == ElementKind.LOCAL_VARIABLE) {
                checker.report(Result.warning("local.cycle.detection.warning",
                                                var.getSimpleName().toString()),
                                node);
            } else {
                Set<String> visited = new HashSet<String>();
                visited.add(node.getName().toString());
                checkCycle(node, getCurrentPath(), visited, Arrays.asList(anno.before()));
            }
        }
        return super.visitVariable(node, p);
    }

    private void checkCycle(VariableTree varTree, TreePath contextPath,
            Set<String> visited, List<String> newBefore) {
        if (newBefore == null || newBefore.size() == 0) return;
        for (String s : newBefore) {
            if (visited.contains(s)) {
                checker.report(Result.failure("lock.order.cycle"), varTree);
                return;
            } else {
                visited.add(s);
                Resolver2 resolver = new Resolver2(checker.getProcessingEnvironment());
                Element variableRef = resolver.findVariable(s, contextPath);

                if(variableRef == null) {
                    checker.errorAbort("Unknown reference" + s);
                    return;
                }

                LockOrder anno = variableRef.getAnnotation(LockOrder.class);
                if (anno != null)
                    checkCycle(varTree, contextPath, visited,
                            Arrays.asList(anno.before()));
                visited.remove(s);
            }
        }
    }
}
