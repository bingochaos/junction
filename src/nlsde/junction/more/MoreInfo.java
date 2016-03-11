 package nlsde.junction.more;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import nlsde.junction.GuideActivity;
import nlsde.junction.R;
import nlsde.tools.BitmapDecode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MoreInfo extends Activity {

	private ImageView imageViewbanner;
	private RelativeLayout pingfenLayout;
	private RelativeLayout guanyuLayout;
	private RelativeLayout fankuiLayout;
	private RelativeLayout fenxiangLayout;
	private RelativeLayout welcomlLayout;
	private RelativeLayout shuniujianjieLayout;
	private Bitmap banner;
	private SnsPostListener mSnsPostListener;
	// 首先在您的Activity中添加如下成员变量
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.moreinfo);
		imageViewbanner = (ImageView) findViewById(R.id.moreinfobanner);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		banner = BitmapDecode.readBitMap(getApplicationContext(),
				R.drawable.banner);
		BitmapDrawable bd = new BitmapDrawable(this.getResources(), banner);
		imageViewbanner.setBackgroundDrawable(bd);
		pingfenLayout = (RelativeLayout) findViewById(R.id.pingfen);
		guanyuLayout = (RelativeLayout) findViewById(R.id.guanyu);
		fankuiLayout = (RelativeLayout) findViewById(R.id.fankui);
		fenxiangLayout = (RelativeLayout) findViewById(R.id.fenxiang);
		welcomlLayout = (RelativeLayout) findViewById(R.id.welcome);
		shuniujianjieLayout =(RelativeLayout)findViewById(R.id.shuniujianjie);
		
		fenxiangLayout = (RelativeLayout) findViewById(R.id.fenxiang);
		shuniujianjieLayout .setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MoreInfo.this,IntroductionList.class));
				
			}
		});
		fenxiangLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String appID = "wxe8165d42f6fab5a5";
				String appSecret = "f86544e52d8881e4abe623c0dcf7f94e";
				mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
						SHARE_MEDIA.WEIXIN_CIRCLE);
				
				// 添加微信平台
				UMWXHandler wxHandler = new UMWXHandler(MoreInfo.this, appID,
						appSecret);
				wxHandler.setTitle("枢纽通");
				wxHandler.addToSocialSDK();
				// 设置微信好友分享内容
				WeiXinShareContent weixinContent = new WeiXinShareContent(new UMImage(getApplicationContext(), BitmapDecode.readBitMap(getApplicationContext(), R.drawable.ic_launcher)));
				// 设置分享文字
				weixinContent
						.setShareContent("枢纽通是面向北京出行人群提供北京各大公共交通枢纽室内地图定位换乘导航及其周边的公交车、地铁、机场交通、长途大巴、公共自行车租赁等交通动静态信息的一体化出行服务软件");
				//weixinContent.setShareImage(new UMImage(getApplicationContext(), BitmapDecode.readBitMap(getApplicationContext(), R.drawable.erweima)));
				// 设置分享内容跳转URL
				weixinContent.setTargetUrl("http://www.wandoujia.com/apps/nlsde.junction?utm_source=share_intent&utm_campaign=app&utm_medium=p4");
				mController.setShareMedia(weixinContent);
				
				
				// 添加微信朋友圈
				UMWXHandler wxCircleHandler = new UMWXHandler(MoreInfo.this,
						appID, appSecret);
				wxCircleHandler.setTitle("枢纽通");
				wxCircleHandler.setToCircle(true);
				wxCircleHandler.addToSocialSDK();
				
				// 设置微信朋友圈分享内容
				CircleShareContent circleMedia = new CircleShareContent(new UMImage(getApplicationContext(), BitmapDecode.readBitMap(getApplicationContext(), R.drawable.ic_launcher)));
				circleMedia.setShareContent("枢纽通是面向北京出行人群提供北京各大公共交通枢纽室内地图定位换乘导航及其周边的公交车、地铁、机场交通、长途大巴、公共自行车租赁等交通动静态信息的一体化出行服务软件");
				circleMedia.setTargetUrl("http://www.wandoujia.com/apps/nlsde.junction?utm_source=share_intent&utm_campaign=app&utm_medium=p4");
				circleMedia.setTitle("枢纽通");
				//circleMedia.setShareImage(new UMImage(getApplicationContext(), BitmapDecode.readBitMap(getApplicationContext(), R.drawable.erweima)));
				mController.setShareMedia(circleMedia);
				

				mController.openShare(MoreInfo.this, false);
				mSnsPostListener = new SnsPostListener() {

					@Override
					public void onStart() {

					}

					@Override
					public void onComplete(SHARE_MEDIA platform, int stCode,
							SocializeEntity entity) {
						if (stCode == 200) {
							Toast.makeText(MoreInfo.this, "分享成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(MoreInfo.this,
									"分享失败 : error code : " + stCode,
									Toast.LENGTH_SHORT).show();
						}
					}
				};
				mController.registerListener(mSnsPostListener);

			}
		});
		welcomlLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MoreInfo.this, GuideActivity.class));
				MoreInfo.this.finish();
			}
		});
		fankuiLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Intent intent = new
				// Intent(MoreInfo.this,AdviseActivity.class);
				// startActivity(intent);
				FeedbackAgent agent = new FeedbackAgent(MoreInfo.this);
				agent.startFeedbackActivity();
			}
		});
		guanyuLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MoreInfo.this, AboutActivity.class));

			}
		});
		pingfenLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse("market://details?id="
						+ "nlsde.junction");
				intent.setData(content_url);
				startActivity(intent);

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (banner != null) {
			banner.recycle();
		}
		mController.unregisterListener(mSnsPostListener);
		super.onDestroy();
	}

}
