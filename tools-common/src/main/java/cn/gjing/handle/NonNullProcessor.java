package cn.gjing.handle;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Gjing
 **/
@SupportedAnnotationTypes("cn.gjing.annotation.NotNull")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class NonNullProcessor extends AbstractProcessor {

    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            //拿到所有带有该注解的
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(annotation);
            elementsAnnotatedWith.forEach(element -> {
                JCTree jcTree = trees.getTree(element);
                if (element.getKind() == ElementKind.METHOD) {
                    jcTree.accept(new TreeTranslator() {
                        @Override
                        public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                            List<JCTree.JCVariableDecl> parameters = jcMethodDecl.getParameters();
                            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                            for (JCTree.JCVariableDecl parameter : parameters) {
                                //过滤掉基本类型
                                if (parameter.getType().type.isPrimitive()) {
                                    continue;
                                }
                                //拿到这个参数上的所有注解
                                java.util.List<String> collect = parameter.mods.annotations.stream().map(JCTree::toString).collect(Collectors.toList());
                                if (collect.contains("@ExcludeParam()")) {
                                    continue;
                                }
                                statements.append(
                                        treeMaker.If(
                                                treeMaker.Binary(
                                                        JCTree.Tag.EQ,
                                                        treeMaker.Ident(names.fromString(parameter.getName().toString())),
                                                        treeMaker.Literal(TypeTag.BOT, null))
                                                ,
                                                treeMaker.Block(0, List.of(
                                                        treeMaker.Throw(
                                                                treeMaker.NewClass(
                                                                        null,
                                                                        List.nil(),
                                                                        buildExceptionClassExpression(treeMaker, names),
                                                                        List.of(treeMaker.Literal(
                                                                                "The parameter '"+parameter.getName().toString()+"' has been used @NotNull, so it cannot be null.")),
                                                                        null
                                                                )
                                                        )
                                                ))
                                                ,
                                                null
                                        )
                                );
                            }
                            jcMethodDecl.body.stats = jcMethodDecl.getBody().getStatements().prependList(statements.toList());
                            super.visitMethodDef(jcMethodDecl);
                        }
                    });
                }
            });
        }
        return true;
    }

    private static JCTree.JCExpression buildExceptionClassExpression(TreeMaker factory, Names symbolsTable) {
        String[] parts = "java.lang.NullPointerException".split("\\.");
        JCTree.JCIdent identifier = factory.Ident(symbolsTable.fromString(parts[0]));
        JCTree.JCFieldAccess selector = null;
        for (int i = 1; i < parts.length; i++) {
            selector = factory.Select(selector == null ? identifier : selector, symbolsTable.fromString(parts[i]));
        }
        return selector == null ? identifier : selector;
    }
}
