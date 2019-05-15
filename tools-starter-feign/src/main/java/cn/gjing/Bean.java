package cn.gjing;

import lombok.Getter;

/**
 * @author Gjing
 **/
@Getter
 enum Bean {
    /**
     *
     */
    FEIGN_PROCESS("feignProcess");

    private String beanName;

    Bean(String beanName) {
        this.beanName = beanName;
    }


}
