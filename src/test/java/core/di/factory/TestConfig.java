package core.di.factory;

import core.annotation.Bean;
import core.annotation.ComponentScan;
import core.annotation.Configuration;

@Configuration
@ComponentScan
public class TestConfig {
    @Bean
    public TestA testA(TestB testB, TestD testd) {
        return new TestA(testB, testd);
    }

    @Bean
    public TestB testB(TestC testC) {
        return new TestB(testC);
    }

    @Bean
    public TestC testC() {
        return new TestC();
    }

    @Bean
    public TestD testD() {
        return new TestD();
    }
}

class TestA {
    TestB testB;
    TestD testD;

    public TestA(TestB testB, TestD testD) {
        this.testB = testB;
        this.testD = testD;
    }

    public TestB getTestB() {
        return this.testB;
    }

    public TestD getTestD() {
        return this.testD;
    }
}

class TestB {
    TestC testC;

    public TestB(TestC testC) {
        this.testC = testC;
    }

    public TestC getTestC() {
        return this.testC;
    }
}

class TestC {

}

class TestD {

}