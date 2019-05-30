package cn.gjing;

/**
 * @author Gjing
 **/
 enum Bean {
    /**
     *
     */
    FEIGN_PROCESS("feignProcess");

    public String getBeanName() {
        return beanName;
    }

    private String beanName;

    Bean(String beanName) {
        this.beanName = beanName;
    }


}
