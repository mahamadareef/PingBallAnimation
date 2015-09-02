package net.leolink.android.simpleinfinitecarousel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment {
	
	public static Fragment newInstance(MainActivity context, int pos, float scale) {
		Bundle b = new Bundle();
		b.putInt("pos", pos);
		b.putFloat("scale", scale);
		return Fragment.instantiate(context, MyFragment.class.getName(), b);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			Log.d("Test", "Container is null");
			return null;
		}
		
		View layout = inflater.inflate(R.layout.mf, container, false);
		
		int pos = this.getArguments().getInt("pos");
		TextView tv = (TextView) layout.findViewById(R.id.text);
		tv.setText("Position = " + pos);
		
		MyLinearLayout root = (MyLinearLayout) layout.findViewById(R.id.root);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);
		
		return layout;
	}
}
