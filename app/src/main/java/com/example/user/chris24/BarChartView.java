package com.example.user.chris24;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 本類別是用來建立長條圖的類別.
 *
 * @author brad@honixtech.com (Brad Chao)
 */
public class BarChartView extends FrameLayout {
    private Context context;							// 呼叫的Activity之物件實體
    private BarImageView[][] imgBar;					// 個別呈現單格的影像元素
    private LayoutParams lpBar, lpThis;				// 版面配置: 單格/本物件
    private int nums, levels;						// 長條數量, 單格個數(層次)
    private int barWidth, barHeight;					// 單格的寬高像素
    private int gapX, gapY;							// 單格的x,y間隔像素
    private int resBarImage0, resBarImage1;			// 呈現的數值的影像資源: 有/無
    private int[] intValues;							// 實際的呈現數值資料
    private int intMinValue, intMaxValue, intRange;	// 最小值, 最大值, 單格差值
    private int lastX = -1, lastY = -1;				// 觸控感應上一次的x,y值
    private boolean isTouchable;						// 是否可以觸控變更資料數值

    /**
     * @param context 呼叫的Activity之物件實體
     * @param attrs 版面配置屬性物件
     * @param isTouchable 是否啟用觸控改變顯示狀態
     * @param intMinValue 最小範圍數值
     * @param intMaxValue 最大範圍數值
     * @param nums 長條個數
     * @param levels 單一數值呈現個數
     * @param barWidth 單一格呈現寬度
     * @param barHeight 單一格呈現高度
     * @param gapX 長條的間隔像素
     * @param gapY 單一格上下間隔像素
     * @param resBarImage0 單一格背景影像
     * @param resBarImage1 單一格呈現影像
     */
    public BarChartView(Context context, AttributeSet attrs,
                        boolean isTouchable,
                        int intMinValue, int intMaxValue,
                        int nums, int levels,
                        int barWidth, int barHeight,
                        int gapX, int gapY,
                        int resBarImage0, int resBarImage1) {
        super(context, attrs);

        this.context = context;
        this.isTouchable = isTouchable;
        this.nums = nums;
        this.levels = levels;
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.gapX = gapX;
        this.gapY = gapY;
        this.resBarImage0 = resBarImage0;
        this.resBarImage1 = resBarImage1;
        this.intMinValue = intMinValue;
        this.intMaxValue = intMaxValue;
        this.intRange = (intMaxValue - intMinValue) / levels;

        imgBar = new BarImageView[nums][levels];
        lpBar = new LayoutParams(barWidth, barHeight);
        lpThis = new LayoutParams((barWidth + gapX)*nums, (barHeight + gapY)*levels);

        intValues = new int[nums];

        initView();
    }

    private void initView(){
        for (int x = 0; x < imgBar.length; x++) {
            for (int y = 0; y < imgBar[x].length; y++) {
                imgBar[x][y] = new BarImageView(context);
                imgBar[x][y].setIndexXY(x, y);
                imgBar[x][y].setLayoutParams(lpBar);
                imgBar[x][y].setImageResource(resBarImage0);
                imgBar[x][y].setX(0 + x * (barWidth + gapX));
                imgBar[x][y].setY(0 + (imgBar[x].length - y) * (barHeight + gapY) - barHeight);
                addView(imgBar[x][y]);
            }
        }
        setLayoutParams(lpThis);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchable) return false;

        float x = event.getX(), y = event.getY();
        int tempx = (int) (x / (barWidth + gapX));
        int tempy = levels + 1 - (int) (y / (gapY + barHeight));
        if (tempx >= 0 && tempx < nums && tempy >= -1 && tempy < levels) {
            if (tempx != lastX || tempy != lastY) {
                intValues[tempx] = tempy+1;
            }
            lastX = tempx;
            lastY = tempy;
        }
        updateValues();
        return true;
    }

    private void updateValues(){
        for (int x = 0; x < imgBar.length; x++) {
            for (int y = 0; y < imgBar[x].length; y++) {
                if (y<intValues[x] / intRange){
                    imgBar[x][y].setImageResource(resBarImage1);
                }else{
                    imgBar[x][y].setImageResource(resBarImage0);
                }
            }
        }
    }

    /**
     * @param intValues 呈現數值陣列資料
     */
    public void setValues(int[] intValues){
        this.intValues = intValues;
        updateValues();
    }

    /**
     * @return 傳回目前整數數值陣列資料
     */
    public int[] getValues(){
        return this.intValues;
    }

    private class BarImageView extends ImageView {
        private int x, y;

        public BarImageView(Context context) {
            super(context);
        }

        void setIndexXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int getIndexX() {
            return x;
        }

        int getIndexY() {
            return y;
        }
    }


}
