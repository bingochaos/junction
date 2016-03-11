package nlsde.junction.topbar;

import nlsde.junction.R;
import nlsde.tools.DensityUtil;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBar extends RelativeLayout {
	private Button leftBtn,rightBtn;  
    private TextView title;  
    private TopBarClickListener topBarClickListener;  
    private String titleStr;  
      

	private RelativeLayout.LayoutParams leftBtnLayoutParams,rightBtnLayoutParams,titleLayoutParams;  
    private static int LEFT_BTN_ID = 1;  
    private static int TITLE_ID = 2;  
    private static int RIGHT_BTN_ID = 3;  
      
    private Drawable leftBackground,rightBackground;  
    private String leftText,rightText;  
    private int leftTextColor,rightTextColor,titleTextColor;  
    private float titleTextSize;  
    private Context context;
      
    public TopBar(Context context,AttributeSet attrs){  
        super(context,attrs);  
          
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.TopBar);  
          
        this.titleStr = ta.getString(R.styleable.TopBar_title);  
        this.leftBackground = ta.getDrawable(R.styleable.TopBar_leftBackground);  
        this.rightBackground = ta.getDrawable(R.styleable.TopBar_rightBackground);  
        this.leftText = ta.getString(R.styleable.TopBar_leftText);  
        this.rightText = ta.getString(R.styleable.TopBar_rightText);  
        this.leftTextColor = ta.getColor(R.styleable.TopBar_leftTextColor, 0);  
        this.rightTextColor = ta.getColor(R.styleable.TopBar_rightTextColor, 0);  
        this.titleTextSize = ta.getDimension(R.styleable.TopBar_titleTextSize, DensityUtil.dip2px(context, 10));  
        this.titleTextColor = ta.getColor(R.styleable.TopBar_titleTextColor, 0);  
        this.context = context;  
        ta.recycle();  
          
        leftBtn = new Button(context);  
        rightBtn = new Button(context);  
        title = new TextView(context);  
          
        leftBtn.setId(LEFT_BTN_ID);  
        rightBtn.setId(RIGHT_BTN_ID);  
        title.setId(TITLE_ID);  
          
          
        leftBtnLayoutParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(this.context, (float) 40),DensityUtil.dip2px(this.context, (float) 40));
        rightBtnLayoutParams = new RelativeLayout.LayoutParams(DensityUtil.dip2px(this.context, (float) 40),DensityUtil.dip2px(this.context, (float) 40)); 
       // leftBtnLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);  
        //rightBtnLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);  
        titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT); 
       // titleLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
          
        leftBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);  
        leftBtnLayoutParams.setMargins(30, 0, 0, 0);//左上右下  
        leftBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);  
          
        rightBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);  
        rightBtnLayoutParams.setMargins(0, 0, 30, 0);//左上右下  
        rightBtnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);  
          
      //  titleLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);  
        titleLayoutParams.setMargins(0, 0, 0, 0);//左上右下  
        titleLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);  
        titleLayoutParams.addRule(RelativeLayout.LEFT_OF, RIGHT_BTN_ID);  
        titleLayoutParams.addRule(RelativeLayout.RIGHT_OF, LEFT_BTN_ID);  
        titleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);  
          
        addView(leftBtn, leftBtnLayoutParams);  
        addView(rightBtn,rightBtnLayoutParams);  
        addView(title,titleLayoutParams);  
          
        leftBtn.setBackgroundDrawable(leftBackground);  
        leftBtn.setText(leftText);  
        leftBtn.setTextColor(leftTextColor);  
        rightBtn.setBackgroundDrawable(rightBackground);  
        rightBtn.setText(rightText);  
        rightBtn.setTextColor(rightTextColor);  
          
        title.setTextSize(DensityUtil.dip2px(this.context, (float) 15));  
        title.setTextColor(Color.WHITE);  
        title.setEllipsize(TruncateAt.MIDDLE);  
         
        title.setSingleLine(true);  
        title.setText(titleStr);  
        title.setTextSize(titleTextSize);  
        title.setTextColor(titleTextColor);  
        title.setGravity(Gravity.CENTER);  
        leftBtn.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(topBarClickListener!=null){  
                    topBarClickListener.leftBtnClick();  
                }  
            }  
        });  
          
        rightBtn.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if(topBarClickListener!=null){  
                    topBarClickListener.rightBtnClick();  
                }  
            }  
        });  
    }  
    public void setLeftButton(int draw){//设置左按钮
    	leftBtn.setBackgroundResource(draw);
    }
    public void setRightButton(int draw){//设置右按钮
    	rightBtn.setBackgroundResource(draw);
    }
    public void setRightText(String text){//设置右按钮
    	rightBtn.setText(text);	
    	
    }
    public void setTitle(String text){//设置标题栏
    	title.setText(text);
    }
    public void setTopBarClickListener(TopBarClickListener topBarClickListener) {  
        this.topBarClickListener = topBarClickListener;  
    }  
}
