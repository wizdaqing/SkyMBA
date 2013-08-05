package com.mba.view;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import cn.wiz.sdk.WizWindow;
import cn.wiz.sdk.api.WizAsyncAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAction;
import cn.wiz.sdk.api.WizAsyncAction.WizAsyncActionThread;
import cn.wiz.sdk.api.WizObject.WizAccount;
import cn.wiz.sdk.settings.WizAccountSettings;

import com.example.skymba.R;
import com.mba.json.ProcessJson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SkyMBASuggestFeedback extends Activity {
	private TextView titleFeedback;
	private TextView suggestOne;
	private TextView suggestTwo;
	private TextView suggestThree;

	private CheckBox suggestOneBox;
	private CheckBox suggestTwoBox;
	private CheckBox suggestThreeBox;

	private EditText wordText;
	private EditText phoneText;

	private Button submitBtn;
	private String urlMain = "http://mba.trends-china.com/service.ashx?";
	boolean[] checked = new boolean[3];
	private String feedback = null;
	private String url = null;
	private JSONObject object = null;
	private CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_feedback);
		ini();

	}

	
	public void ini() {
		titleFeedback = (TextView) this.findViewById(R.id.suggest_feedbackView);
		suggestOne = (TextView) this.findViewById(R.id.suggest_one);
		suggestTwo = (TextView) this.findViewById(R.id.suggest_two);
		suggestThree = (TextView) this.findViewById(R.id.suggest_three);
		suggestOneBox = (CheckBox) this.findViewById(R.id.checkBoxSuggestOne);
		suggestTwoBox = (CheckBox) this.findViewById(R.id.checkBoxSuggestTwo);
		suggestThreeBox = (CheckBox) this
				.findViewById(R.id.checkBoxSuggestThree);
		wordText = (EditText) this.findViewById(R.id.i_have_word_to_say);
		phoneText = (EditText) this.findViewById(R.id.phoneText);
		submitBtn = (Button) this.findViewById(R.id.submit_suggest);
		checkBox=(CheckBox)this.findViewById(R.id.defaultButton);
		checkBox.setChecked(false);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					String email=WizAccountSettings.getUserId(SkyMBASuggestFeedback.this);
					if(email==null|email.equals("")){
						Toast.makeText(SkyMBASuggestFeedback.this, "您还没有注册", Toast.LENGTH_SHORT).show();
					}else{
					phoneText.setText(email);
					}
				}else{
					phoneText.setText("");
				}
			}
		});
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				submitFeedback();
			}
		});
	}

	public void submitFeedback() {
		feedback=null;
		checked[0] = suggestOneBox.isChecked();
		checked[1] = suggestTwoBox.isChecked();
		checked[2] = suggestThreeBox.isChecked();
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < 3; j++) {
			if (checked[j] == true) {
				sb.append("" + (j + 1));
				sb.append(",");
			}
		}

		sb.toString();
		if (sb.length() != 0) {
			int dot = sb.lastIndexOf(",");
			feedback = sb.substring(0, dot);// 得到拼接的feedback
			
		}

		if (feedback == null&&wordText.getText().toString().equals("")) {
			Toast.makeText(SkyMBASuggestFeedback.this, "意见反馈不完整",
					Toast.LENGTH_SHORT).show();

		} 

		else {
			url = urlMain + "comm=addfeedback&ctype=1&mobile="
					+ phoneText.getText().toString() + "&feedback=" + feedback
					+ "&content=" + wordText.getText().toString();

			// 网络连接获取数据：

			WizAsyncAction.startAsyncAction(null, new WizAction() {

				@Override
				public Object work(WizAsyncActionThread thread,
						Object actionData) throws Exception {
					String result = null;
					DefaultHttpClient httpClient = new DefaultHttpClient();

					String urlFull = url;
					HttpGet request = new HttpGet(urlFull);
					HttpResponse response;
					try {
						response = httpClient.execute(request);
						result = EntityUtils.toString(response.getEntity());
						object = new JSONObject(result);

					} catch (ClientProtocolException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					} catch (JSONException e) {

						e.printStackTrace();
					}

					return object;
				}

				@Override
				public void onBegin(Object actionData) {

				}

				@Override
				public void onEnd(Object actionData, Object ret) {

					int success = ProcessJson.doSubmitSuggest(object);
					if (success == 1) {
						WizWindow.showMessage(SkyMBASuggestFeedback.this,
								R.string.submit_suggest_success);
					} else if (success == 0) {
						WizWindow.showMessage(SkyMBASuggestFeedback.this,
								R.string.submit_suggest_fail);
					}

				}

				@Override
				public void onException(Object actionData, Exception e) {
					e.printStackTrace();
					Toast.makeText(SkyMBASuggestFeedback.this,
							"网络传输出错，请检查您的网络", Toast.LENGTH_LONG).show();

				}

				@Override
				public void onStatus(Object actionData, String status,
						int arg1, int arg2, Object obj) {

				}

			});
		}

	}
}
