package core.mvc.tobe;

import core.di.factory.example.QnaController;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationScannerTest {

    @Test
    void AnnotationScanner으로_basePackage_설정() {
        AnnotationScanner annotationScanner = new AnnotationScanner();

        assertThat(annotationScanner.getTypes()).contains(QnaController.class);
    }
}