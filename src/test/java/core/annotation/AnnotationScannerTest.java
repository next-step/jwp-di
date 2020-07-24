package core.annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationScannerTest {

    @DisplayName("어노테이션 package 에서 특정 어노테이션을 상속하는 모든 어노테이션 찾기")
    @Test
    void findAnnotationsAnnotatedBy() {
        /* given */
        AnnotationScanner annotationScanner = new AnnotationScanner();

        /* when */
        Set<Class<? extends Annotation>> annotations = annotationScanner.scan(ComponentScan.class);

        /* then */
        assertThat(annotations).containsExactlyInAnyOrder(ComponentScan.class, WebApplication.class);
    }

}
