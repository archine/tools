package com.gjing.handle;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author Archine
 **/
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.gjing.annotation.Get")
public class GetterProcessor extends AbstractProcessor {
    /**
     * Messager use to log
     * JavacTrees prepare offer AST
     * TreeMaker use to create AST
     * Names create symbol
     */
    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            //拿到所有设置有getter注解的类
            Set<? extends Element> elementsAnnotated = roundEnv.getElementsAnnotatedWith(annotation);
            System.out.println("所有有这个注解的类: " + elementsAnnotated.toString());
            elementsAnnotated.forEach(element -> {
                //遍历所有元素,并生成这个element的语法树
                JCTree jcTree = trees.getTree(element);
                System.out.println("语法书:" + jcTree.toString());
                jcTree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                        List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();
                        for (JCTree tree : jcClassDecl.defs) {
                            if (tree.getKind().equals(Tree.Kind.VARIABLE)) {
                                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                                jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
                            }
                        }
                        System.out.println("变量定义: " + jcVariableDeclList.toString());
                        jcVariableDeclList.forEach(jcVariableDecl -> jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetterMethodDecl(jcVariableDecl)));
                        super.visitClassDef(jcClassDecl);
                    }
                });

            });
            return false;
        }
        return true;
    }

    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCVariableDecl jcVariableDecl) {

        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.getName())));
        System.out.println("statement: " + statements.toString());
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getNewMethodName(jcVariableDecl.getName()), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
    }

    private Name getNewMethodName(Name name) {
        String s = name.toString();
        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1, name.length()));
    }

}
