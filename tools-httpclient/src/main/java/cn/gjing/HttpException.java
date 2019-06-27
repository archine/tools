package cn.gjing;

/**
 * @author Gjing
 **/
class HttpException extends RuntimeException {
    private static final long serialVersionUID = 260136951169545885L;

    HttpException(String message) {
        super(message);
    }
}
