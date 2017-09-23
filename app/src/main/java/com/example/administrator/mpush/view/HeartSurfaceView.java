package com.example.administrator.mpush.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.administrator.mpush.R;
import com.example.administrator.mpush.utils.ScreenUtils;
import com.example.administrator.mpush.utils.SizeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by yun.liu@avazu.net on 2016/3/21.
 * 当百分比超过1时，删除飘心
 *
 */
public class HeartSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private List<BitmapBean> mDisplayList = new ArrayList<>();
    private SurfaceHolder mSurfaceHolder;
    private boolean mIsDrawOk = false;
    private Context context;
    private int screenHeight, end_y, start_y;
    private Map<Integer, Bitmap> heartMap = new HashMap<>();//最多添加30个
    private Bitmap board = null;//空白的画布
    private Canvas boardCanvas = null;//board 上做图的画布
    private Rect mRect = new Rect();

    public HeartSurfaceView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public HeartSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public HeartSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private int widthMeasureSpec, heightMeasureSpec;
    private boolean isFirst = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isFirst) {
            this.widthMeasureSpec = widthMeasureSpec;
            this.heightMeasureSpec = heightMeasureSpec;
        }
        isFirst = false;
        super.onMeasure(this.widthMeasureSpec, this.heightMeasureSpec);
    }

    private void initData() {
        heartMap.put(R.mipmap.heart_1, BitmapFactory.decodeResource(getResources(), R.mipmap.heart_1));
        heartMap.put(R.mipmap.heart_2, BitmapFactory.decodeResource(getResources(), R.mipmap.heart_2));
        heartMap.put(R.mipmap.heart_3, BitmapFactory.decodeResource(getResources(), R.mipmap.heart_3));
        heartMap.put(R.mipmap.heart_4, BitmapFactory.decodeResource(getResources(), R.mipmap.heart_4));
    }

    private Paint paint;
    private DrawThread drawThread;
    private boolean isRunning = true;

    private void init() {
        drawThread = new DrawThread();

        //添加图片
        initData();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        setZOrderMediaOverlay(true);
        // View是否转换成保存他的图像   没有缓存，不能截图不能将view转换为bitmap
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);//视图不是自动绘制
        setZOrderOnTop(true);//当前sureface置于Activity显示窗口的最顶层才能正常显示
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        screenHeight = ScreenUtils.getScreenHeight(context);
        start_y = screenHeight - SizeUtils.dp2Px(context, 77) - ScreenUtils.getStatusBarHeight(context);
        end_y = SizeUtils.dp2Px(context, 52);
    }

    public void addHeart() {
        //随机返回一个drawable id
        int drawableId;
            drawableId = random1();
        BitmapBean bitmapBean = new BitmapBean();
        bitmapBean.setBitmap(heartMap.get(drawableId));
        bitmapBean.setP1(new PointF(150, start_y));
        int offSetWidth = getOffSetWidth();
        bitmapBean.setP2(new PointF(offSetWidth, start_y / 3));
        int offSetWidth1 = getOffSetWidth();
        bitmapBean.setP3(new PointF(offSetWidth1, start_y / 3 * 2));
        bitmapBean.setP4(new PointF(150, 300));
        //最多添加30个红点
        if (mDisplayList.size() < 30) {
            mDisplayList.add(bitmapBean);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawOk = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        board = Bitmap.createBitmap(400, this.getHeight(),
                Bitmap.Config.ARGB_8888);
        boardCanvas = new Canvas(board);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawOk = false;
    }

    public void start() {
        drawThread.start();
    }

    public void clear() {
        isRunning = false;
        Iterator<Map.Entry<Integer, Bitmap>> it = heartMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Bitmap> entry = it.next();
            entry.getValue().recycle();
        }
        if (board != null)
            board.recycle();
        heartMap.clear();
    }

    private void draw() {
        synchronized (this) {
            long startTime = System.currentTimeMillis();
            if (!mIsDrawOk) {
                return;
            }
            if (boardCanvas != null) {
                boardCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            for (int i = 0; i < mDisplayList.size(); i++) {
                BitmapBean bitmapBean = mDisplayList.get(i);

                if (bitmapBean.time >= 1) {
                    mDisplayList.remove(i);
                }
            }
            //飘心动画使用的矩形区域
            mRect.left = getWidth() - 400;
            mRect.top = end_y;
            mRect.right = getWidth();
            mRect.bottom = getHeight();
            Canvas canvas = mSurfaceHolder.lockCanvas(mRect);
            if (canvas == null) return;
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            try {
                for (int i = 0; i < mDisplayList.size(); i++) {
                    //根据BitmapBean的属性创建bitmap 的画布
                    BitmapBean bitmapBean = mDisplayList.get(i);
                    bitmapBean.draw();
                }

                if (canvas != null && board != null) {
                    //屏幕右侧部位画图
                    canvas.drawBitmap(board, getWidth() - 400, 0, null);
                }
                long endTime = System.currentTimeMillis();
                long frameRate = endTime - startTime;
                if (frameRate > 20)
                    Log.e("===", " time 2 : " + frameRate);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mSurfaceHolder != null && canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

        }
    }

    /**
     * 计算塞贝儿曲线
     *
     * @param time       时间，范围0-1
     * @param startValue 起始点
     * @param pointF1    拐点1
     * @param pointF2    拐点2
     * @param endValue   终点
     * @return 塞贝儿曲线在当前时间下的点
     */
    public PointF evaluate(float time, PointF startValue, PointF pointF1, PointF pointF2,
                           PointF endValue) {

        float timeLeft = 1.0f - time;
        PointF point = new PointF();//结果
        PointF point0 = startValue;//起点
        PointF point3 = endValue;//终点
        //代入公式
        point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                + 3 * timeLeft * timeLeft * time * (pointF1.x)
                + 3 * timeLeft * time * time * (pointF2.x)
                + time * time * time * (point3.x);

        point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                + 3 * timeLeft * timeLeft * time * (pointF1.y)
                + 3 * timeLeft * time * time * (pointF2.y)
                + time * time * time * (point3.y);
        return point;
    }

    Random random = new Random();

    private int getOffSetWidth() {
        return random.nextInt(400) - random.nextInt(200) + SizeUtils.dp2Px(context, 30);
    }

    /**
     * 得到随机旋转角度
     * @return
     */
    private int getRandomRotate() {
        return random.nextInt(30) - random.nextInt(30);
    }

    public class BitmapBean {
        private Bitmap bitmap;
        private float time = 0;//百分比
        private PointF p1;
        private PointF p2;
        private PointF p3;
        private PointF p4;
        private float size = 0;
        private int alpha = 255;
        private int rotate = getRandomRotate();
        private boolean isRotateLeft = false;

        public PointF getP1() {
            return p1;
        }

        public void setP1(PointF p1) {
            this.p1 = p1;
        }

        public PointF getP2() {
            return p2;
        }

        public void setP2(PointF p2) {
            this.p2 = p2;
        }

        public PointF getP3() {
            return p3;
        }

        public void setP3(PointF p3) {
            this.p3 = p3;
        }

        public PointF getP4() {
            return p4;
        }

        public void setP4(PointF p4) {
            this.p4 = p4;
        }

        public float getTime() {
            return time;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public void draw() {
            float time = getTime();
            PointF point = evaluate(time, getP1(), getP2(), getP3(), getP4());
            time += 0.0039f;
            float x = point.x;
            float y = point.y;

            //设置动画
            // paint: alpha  rotate
            // Matrix:  size  Translate 不断累加
            if (alpha > 0)
                paint.setAlpha(alpha--);
            else
                paint.setAlpha(0);
            if (rotate >= -30 && rotate <= 30) {
                if (rotate == 30) {
                    isRotateLeft = false;
                } else if (rotate == -30) {
                    isRotateLeft = true;
                }
                //旋转到30度时，在旋转到-30°，反复循环
                if (isRotateLeft)
                    rotate++;
                else
                    rotate--;
            }
            Matrix mMatrix = new Matrix();
//            mMatrix.postRotate(rotate);
            if (size < 1)
                size += 0.1;
            mMatrix.postScale(size, size);
            mMatrix.postTranslate(x, y);
            boardCanvas.drawBitmap(getBitmap(), mMatrix, paint);
            setTime(time);
        }

    }

    class DrawThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (isRunning) {
                draw();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int random1() {
        int[] heartArray = {R.mipmap.heart_1, R.mipmap.heart_2, R.mipmap.heart_3, R.mipmap.heart_4};
        int index = (int) (Math.random() * heartArray.length);
        return heartArray[index];
    }

}
