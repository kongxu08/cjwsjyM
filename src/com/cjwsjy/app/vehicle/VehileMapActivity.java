package com.cjwsjy.app.vehicle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.cjwsjy.app.R;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
//import com.amap.api.location.LocationManagerProxy;
//import com.amap.api.location.LocationProviderProxy;
//import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;


public class VehileMapActivity  extends BaseActivity2 implements LocationSource, AMapLocationListener {
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;
    //private LocationManagerProxy mAMapLocationManager;
 
    private double jing;
    private double wei;
    private String desc;
    private TextView v_address;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);
        setHeadView(R.drawable.onclick_back_btn, "", selectVehicleActivity.plate, 0, "", this, this, this);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        jing=getIntent().getDoubleExtra("jing", 114.17);
        wei=getIntent().getDoubleExtra("wei", 30.35);
        desc=getIntent().getStringExtra("desc");
        v_address=(TextView) findViewById(R.id.v_address);
        v_address.setText(desc);
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init()
    {
        int result = 0;
        result = 1;
        if (aMap == null)
        {
            aMap = mapView.getMap();
            setUpMap();

            LatLng marker1 = new LatLng(30.35 , 114.17);		//武汉
            //LatLng marker1 = new LatLng(wei , jing);
    		//设置中心点和缩放比例
    		aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
            //aMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				
				@Override
				public void onCameraChangeFinish(CameraPosition cameraPosition) {
					System.out.println("zoom level is:"+cameraPosition.tilt );
				}
				@Override
				public void onCameraChange(CameraPosition arg0) {
				}
			});
        }
    }

    private void setUpMap()
    {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
        // 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
        //aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);

        switch (v.getId()) {
            case R.id.leftImage:
                finish();
                break;

            default:
                break;
        }
    }
 
    /**
     * 此方法需存在
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
 
    /**
     * 此方法需存在
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }
 
    /**
     * 此方法需存在
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }
 
    /**
     * 此方法已经废弃
     */
//    @Override
//    public void onLocationChanged(Location location) {
//    }
 
    /**
     * 定位成功后回调函数
     */
    AMapLocation amapLocation=null;
    @Override
    public void onLocationChanged(AMapLocation location)
    {
    	if (location != null)
        {
    		location.setLatitude(wei);
    		location.setLongitude(jing);
    	}
        amapLocation = location;
        if (mListener != null && amapLocation != null)
        {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(8));
    }
 
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener)
    {
        /*mListener = listener;
        if (mAMapLocationManager == null)
        {
            mAMapLocationManager = LocationManagerProxy.getInstance(this);
            //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周期调用destroy()方法      
            //其中如果间隔时间为-1，则定位只定一次
            mAMapLocationManager.requestLocationUpdates( LocationProviderProxy.AMapNetwork, 60*1000, 10, this);
        }*/
    }
 
 
    /**
     * 停止定位
     */
    @Override
    public void deactivate()
    {
        /*mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;*/
    }

	/*@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}*/
	
}
