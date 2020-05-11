package cn.gjing.tools.auth.handler;

import cn.gjing.tools.auth.metadata.AuthorizationMetaData;

import java.lang.annotation.Annotation;

/**
 * Authorization annotation handler
 *
 * @author Gjing
 **/
public abstract class AnnotationHandler {
    protected final Class<? extends Annotation> annotationClass;

    public AnnotationHandler(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    /**
     * Get the authorization annotation type of the handler
     *
     * @return Annotation Class
     */
    public Class<? extends Annotation> getAnnotationClass() {
        return this.annotationClass;
    }

    /**
     * Authorization to check
     *
     * @param annotation Authorization annotation
     * @param metaData   AuthorizationMetaData
     */
    public abstract void assertAuthorization(Annotation annotation, AuthorizationMetaData metaData);
}
