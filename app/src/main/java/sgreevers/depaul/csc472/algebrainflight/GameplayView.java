package sgreevers.depaul.csc472.algebrainflight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameplayView extends SurfaceView implements SurfaceHolder.Callback {
    private int centerX, centerY, yaw;
    private boolean done, surfaceAvailble;
    private SurfaceHolder holder;
    private AnswerCloud leftCloud, middleCloud, rightCloud;
    private Answer selectedAnswer;

    enum CloudLocation {
        LEFT,
        MIDDLE,
        RIGHT
    }

    class AnswerCloud {
        Bitmap bitmap;
        private Bitmap originalBitmap;
        private CloudLocation cloudLocation;
        int x, y, dx = 2, dy = 1;
        boolean selected;
        Answer answer;

        AnswerCloud(int resId, CloudLocation cloudLocation) {
            originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
            bitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 50, false);
            this.cloudLocation = cloudLocation;
        }

        void move() {
            int updateWidth = bitmap.getWidth();
            int updateHeight = bitmap.getHeight();
            bitmap = Bitmap.createScaledBitmap(originalBitmap,
                    updateWidth + dx,
                    updateHeight + dy,
                    false);

            switch (cloudLocation) {
                case LEFT:
                    x = centerX - (updateWidth + (updateWidth/2)) - 30 - yaw;
                    y = centerY - updateHeight/2;
                    break;
                case MIDDLE:
                    x = centerX - updateWidth/2 - yaw;
                    y = centerY - updateHeight/2;
                    break;
                case RIGHT:
                    x = centerX + (updateWidth/2) + 30 - yaw;
                    y = centerY - updateHeight/2;
                    break;
            }

            selected = x - 15 < centerX && centerX < (x + updateWidth + 15);
            if (selected) selectedAnswer = answer;
            updateText(updateWidth, updateHeight);
        }

        void updateText(int textX, int textY) {
            bitmap = bitmap.copy(bitmap.getConfig(), true);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(selected ? Color.GREEN : Color.RED);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            float scale = getResources().getDisplayMetrics().density;
            paint.setTextSize((int)(24 * scale));
            paint.setTextAlign(Paint.Align.CENTER);

            String value = answer.getValue();
            paint.getTextBounds(value, 0, value.length(), new Rect());

            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(value, textX/2, textY/2 + 20, paint);
        }

        void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceAvailble = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceAvailble = false;
        stopAnimation();
    }

    public GameplayView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        initClouds();
    }

    public GameplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        initClouds();
    }

    private void initClouds() {
        leftCloud = new AnswerCloud(R.drawable.cloud, CloudLocation.LEFT);
        middleCloud = new AnswerCloud(R.drawable.cloud, CloudLocation.MIDDLE);
        rightCloud = new AnswerCloud(R.drawable.cloud, CloudLocation.RIGHT);
    }

    public boolean isAnswerCorrect() {
        return selectedAnswer.isCorrect();
    }

    public void updateProblem(Problem problem) {
        initClouds();
        yaw = 0;
        Answer[] answers = problem.getAnswers();
        leftCloud.answer = answers[0];
        middleCloud.answer = answers[1];
        rightCloud.answer = answers[2];
    }

    public void startAnimation() {
        done = false;
        if (surfaceAvailble) render();
    }

    public void stopAnimation() {
        done = true;
    }

    public void setYaw(int yaw) {
        this.yaw = centerX - yaw;
    }

    private void render() {
        Rect frame = holder.getSurfaceFrame();
        centerX = frame.centerX();
        centerY = frame.centerY();
        new Thread(new Runnable() {
            public void run() {
                while (!done) {
                    Canvas c = null;
                    try {
                        c = holder.lockCanvas();
                        synchronized (holder) {
                            doDraw(c);
                        }
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);
                        }
                    }
                }
            }
        }).start();
    }

    protected void doDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.material_light_blue_100));
        updateLeftCloud(canvas);
        updateMiddleCloud(canvas);
        updateRightCloud(canvas);
    }

    private void updateLeftCloud(Canvas canvas) {
        leftCloud.move();
        leftCloud.draw(canvas);
    }

    private void updateMiddleCloud(Canvas canvas) {
        middleCloud.move();
        middleCloud.draw(canvas);
    }

    private void updateRightCloud(Canvas canvas) {
        rightCloud.move();
        rightCloud.draw(canvas);
    }
}
