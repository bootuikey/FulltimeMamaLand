package com.letv.skin.base;

import java.util.Observable;
import java.util.Observer;

import com.lecloud.leutils.ReUtils;
import com.letv.skin.BaseView;
import com.letv.universal.notice.UIObserver;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 播放按钮
 * 
 * @author pys
 *
 */
public abstract class BasePlayBtn extends BaseView implements OnClickListener {
	public static int play_btn_type_live = 1;
	public static int play_btn_type_vod = 2;

	private ImageView playBtn;
	// private int playstate;

	private int playBtnType = 2;

	public BasePlayBtn(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public BasePlayBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BasePlayBtn(Context context) {
		super(context);
	}

	abstract protected String getPauseStyle();

	abstract protected String getPlayStyle();

	abstract protected String getLayout();

	/**
	 * init view
	 * 
	 * @param context
	 */
	@Override
	protected void initView(Context context) {

		playBtn = (ImageView) LayoutInflater.from(context).inflate(ReUtils.getLayoutId(context, getLayout()), null);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(playBtn, params);
		setOnClickListener(this);
		reset();
	}

	@Override
	public void onClick(View v) {
		if (isClickable() && player != null) {
			if (playBtnType == play_btn_type_vod) {
				typeVodClick();
			} else if (playBtnType == play_btn_type_live) {
				typeLiveClick();
			}
		}
	}

	private void typeLiveClick() {
		if (player.isPlaying()) {
			player.pause();
		} else {
			player.resetPlay();
		}
	}

	private void typeVodClick() {
		if (player.isPlaying()) {
			player.pause();
			if(uiPlayContext!=null){
				//记录用户按下暂停
				uiPlayContext.setClickPauseByUser(true);
			}
		} else if (player.isPlayCompleted()) {
			player.resetPlay();
		} else {
			player.start();
			if(uiPlayContext!=null){
				//用户按下播放按钮
				uiPlayContext.setClickPauseByUser(false);
			}
		}
	}

	private boolean isComplete;//是否已经播放完成

	@Override
	protected void initPlayer() {
		/**
		 * 可能刚开始创建的时候，播放器已经在播放，收不到状态
		 */
		showBtnState();
		player.attachObserver(new UIObserver() {
			@Override
			public void update(Observable observable, Object data) {
				showBtnState();
			}
		});
	}
	
	private void showBtnState() {
		if (player.isPlaying()) {
			showPauseState();
		} else {
			showPlayState();
		}
	}

	/**
	 * 展示播放状态
	 */
	public void showPlayState() {
		playBtn.setImageResource(ReUtils.getDrawableId(context, getPlayStyle()));
	}

	/**
	 * 展示暂停状态
	 */
	public void showPauseState() {
		playBtn.setImageResource(ReUtils.getDrawableId(context, getPauseStyle()));
	}

	/**
	 * 初始化按钮状态
	 */
	public void reset() {
		showPlayState();
		isComplete = false;
	}

	public int getPlayBtnType() {
		return playBtnType;
	}

	/**
	 * 设置当前播放器按钮所需要的类型
	 * 
	 * @param playBtnType
	 */
	public void setPlayBtnType(int playBtnType) {
		this.playBtnType = playBtnType;
	}
}
