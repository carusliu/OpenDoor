package com.carusliu.opendoor.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carusliu.opendoor.R;
import com.carusliu.opendoor.modle.Prize;
import com.carusliu.opendoor.network.NBRequest;
import com.carusliu.opendoor.sysconstants.SysConstants;
import com.carusliu.opendoor.tool.AsyncImageLoader;
import com.carusliu.opendoor.tool.SharedPreferencesHelper;
import com.carusliu.opendoor.tool.SharedPreferencesKey;
import com.carusliu.opendoor.tool.AsyncImageLoader.ImageCallback;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

public class PrizeDetail extends HWActivity implements OnClickListener {

	private TextView leftText, title, rightText;
	private TextView tvPrizeUse, tvPrizeInfo, tvPrizeId, tvPrizeAddress,
			tvPrizePhone, tvPrizeCipher, tvPrizeProvider;
	private ImageView promote_rate, shareBtn, prizePic;
	private Prize prize;
	AsyncImageLoader asyncImageLoader;
	private static final int CODE_BALANCE = 3;
	private static final int CODE_DELETE = 4;
	private static final int CODE_ORDER = 5;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prize_detail);

		Intent intent = getIntent();
		if (intent != null) {
			prize = (Prize) intent.getSerializableExtra("prize");
		}

		if (prize == null) {
			prize = new Prize();
		}
		asyncImageLoader = new AsyncImageLoader(this);
		initView();

	}

	public void initView() {

		// ����ëǮ����н����¼���
		promote_rate = (ImageView) findViewById(R.id.btn_promote_rate);
		shareBtn = (ImageView) findViewById(R.id.btn_share);
		leftText = (TextView) findViewById(R.id.btn_left);
		title = (TextView) findViewById(R.id.tv_center);
		rightText = (TextView) findViewById(R.id.btn_right);
		prizePic = (ImageView) findViewById(R.id.prize_pic);
		if (prize != null) {
			((TextView) findViewById(R.id.tx_prize_use)).setText("�����ֽ�����Ʒ");
			((TextView) findViewById(R.id.prize_info)).setText(prize.getInfo());
			((TextView) findViewById(R.id.prize_id)).setText(prize.getNumber());
			((TextView) findViewById(R.id.prize_address)).setText(prize
					.getAddress());
			((TextView) findViewById(R.id.prize_phone)).setText(prize
					.getPhone());
			((TextView) findViewById(R.id.prize_cipher)).setText(prize
					.getCipher());
			((TextView) findViewById(R.id.prize_date)).setText(prize
					.getStartDate());
			((TextView) findViewById(R.id.tx_prize_provider)).setText(prize
					.getProvider());
		}
		title.setText("�콱��");
		leftText.setText("<����");
		rightText.setText("ɾ��");

		rightText.setOnClickListener(this);
		leftText.setOnClickListener(this);
		promote_rate.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		asyncImageLoader.loadBitmap(prize.getSmallPic(), new ImageCallback() {
			public void imageLoaded(Bitmap imageDrawable, String imageUrl) {
				prizePic.setImageBitmap(imageDrawable);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			deleteAwardReuquest();
			break;
		case R.id.btn_share:
			Intent intent = new Intent(PrizeDetail.this, ShareActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_promote_rate:
			promoteRate();
			break;
		}

	}

	public void deleteAwardReuquest() {

		HashMap<String, String> data = new HashMap<String, String>();
		String userId = SharedPreferencesHelper.getString(
				SharedPreferencesKey.USER_ID, "0");
		data.put(SysConstants.USER_ID, userId);
		data.put(SysConstants.AWARD_ID, prize.getId());
		NBRequest nbRequest = new NBRequest();
		nbRequest.setRequestTag(CODE_DELETE);
		nbRequest.sendRequest(m_handler, SysConstants.DELETE_AWARD_URL, data,
				SysConstants.CONNECT_METHOD_GET, SysConstants.FORMAT_JSON);
	}

	@Override
	public void parseResponse(NBRequest request) {
		// TODO Auto-generated method stub
		System.out.println(request.getCode());
		if (request.getCode().equals(SysConstants.ZERO)) {
			JSONObject jsonObject = request.getBodyJSONObject();
			switch (request.getRequestTag()) {
			case CODE_BALANCE:

				break;

			case CODE_DELETE:
				Toast.makeText(getApplicationContext(), "�h���ɹ�",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			}

		} else {
			Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		System.out.println(str);
		if (str.equals("success")) {
			Toast.makeText(getApplicationContext(), "֧���ɹ�", Toast.LENGTH_SHORT)
			.show();
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(getApplicationContext(), "����ʧ��", Toast.LENGTH_SHORT)
			.show();
		} else if (str.equalsIgnoreCase("cancel")) {
			Toast.makeText(getApplicationContext(), "����ȡ��֧��", Toast.LENGTH_SHORT)
			.show();
		}
	}

	public void promoteRate() {

		String isLogin = SharedPreferencesHelper.getString(
				SharedPreferencesKey.IS_LOGIN, "0");
		Float balance = SharedPreferencesHelper.getFloat(
				SharedPreferencesKey.BALANCE, 100);
		Float buyRateMinimumRequest = SharedPreferencesHelper.getFloat(
				SharedPreferencesKey.MINIMUM_REQUEST, 120);

		if (isLogin.equals("1")) {
			// �ж����
			if (buyRateMinimumRequest > balance) {
				// ��ǰ����֧��
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("����");
				alert.setMessage("�����˻�����Ѳ��㣬�Ƿ�������ֵ��");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "����лл",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �رնԻ���
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "��",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(PrizeDetail.this,
										RechargeActivity.class);
								startActivity(intent);
							}
						});
				// ��ʾ�Ի���
				alert.show();
			} else {
				// ��ʾ��ǰ�����ѽ��
				AlertDialog alert = new AlertDialog.Builder(this).create();
				alert.setTitle("֧��ȷ��");
				alert.setMessage("����������п۳� " + buyRateMinimumRequest
						+ " Ԫ���Ƿ������");
				alert.setButton(AlertDialog.BUTTON_NEGATIVE, "ȡ��",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// �رնԻ���
								dialog.cancel();
							}
						});
				alert.setButton(AlertDialog.BUTTON_POSITIVE, "��",
						new AlertDialog.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// ֧��
								
							}
						});
				// ��ʾ�Ի���
				alert.show();
			}
		} else {
			// ��ʾ��¼
			// Toast.makeText(this, "����δ��¼�����ȵ�¼", Toast.LENGTH_SHORT).show();
			// �Ƚ���Ʒ��Ϣ������
			Intent intent = new Intent(PrizeDetail.this, Login.class);
			intent.putExtra("from", "PrizeDetail");
			startActivity(intent);
		}
	}

}
