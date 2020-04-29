package cn.gjing.tools.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Gjing
 **/
public final class AuthCodeUtils {
    /**
     * 图片的宽度。
     */
    private int width = 160;
    /**
     * 图片的高度。
     */
    private int height = 40;
    /**
     * 验证码字符个数
     */
    private int codeCount = 4;
    /**
     * 验证码干扰线数
     */
    private int lineCount = 50;
    /**
     * 验证码
     */
    private String code = null;

    private final char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private BufferedImage bufferedImage;


    /**
     * 全部默认
     */
    public AuthCodeUtils() {
        this.createCodeImage();
    }

    /**
     * 默认验证码字符个数
     * @param width 宽度
     * @param height 高度
     */
    public AuthCodeUtils(int width, int height){
        this.width = width;
        this.height = height;
        this.createCodeImage();
    }

    /**
     * 全部自定义
     * @param width 宽度
     * @param height 高度
     * @param codeCount 验证码数量
     * @param lineCount 干扰线数量
     */
    public AuthCodeUtils(int width, int height, int codeCount, int lineCount){
        this(width, height);
        this.codeCount = codeCount;
        this.lineCount = lineCount;
        this.createCodeImage();
    }

    /**
     * 生成验证码到本地文件夹
     * @param localPath 本地目录
     * @throws IOException IOException
     * @return this
     */
    public AuthCodeUtils writeToLocal(String localPath) throws IOException {
        OutputStream outputStream = new FileOutputStream(localPath);
        AuthCodeUtils write = this.write(outputStream);
        outputStream.flush();
        outputStream.close();
        return write;
    }

    /**
     * 用流传输
     * @param outputStream 输出流
     * @throws IOException IOException
     * @return this
     */
    public AuthCodeUtils write(OutputStream outputStream) throws IOException {
        ImageIO.write(bufferedImage, "png", outputStream);
        return this;
    }

    /**
     * 获取验证码图片的数据
     * @return BufferImage对象
     */
    public BufferedImage getBufferedImage(){
        return bufferedImage;
    }

    /**
     * 获取验证码字符串
     * @return 验证码(非图片)
     */
    public String getCode(){
        return code;
    }

    /**
     * 创建验证码
     */
    private void createCodeImage(){
        //字符所在x坐标
        int x;
        //字体高度
        int fontHeight;
        //字符所在y坐标
        int codeY;
        int red;
        int green;
        int blue;
        x = width / (codeCount + 2);
        fontHeight = height - 2;
        codeY = height - 4;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        Random random = new Random();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0, 0, width,height);
        Font font = new Font("Fixedays",Font.PLAIN,fontHeight);
        graphics2D.setFont(font);
        for (int i = 0; i < lineCount; i++) {
            //x轴第一个点的位置
            int x1 = random.nextInt(width);
            //y轴第一个点的位置
            int y1 = random.nextInt(height);
            //x轴第二个点的位置
            int x2 = x1 + random.nextInt(width >> 2);
            //y轴第二个点的位置
            int y2 = y1 + random.nextInt(height >> 2);
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            graphics2D.setColor(new Color(red, green, blue));
            graphics2D.drawLine(x1, y1, x2, y2);
        }
        StringBuilder randomCode = new StringBuilder(codeCount);
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            graphics2D.setColor(new Color(red, green, blue));
            graphics2D.drawString(strRand, (i +1) * x, codeY);
            randomCode.append(strRand);
        }
        code = randomCode.toString();
    }

}
