package com.cjwsjy.app.vehicle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.cjwsjy.app.R;
import com.cjwsjy.app.amap.AMapUtil;
import com.cjwsjy.app.amap.DrivingRouteOverlay;

import java.text.SimpleDateFormat;
import java.util.Date;


public class VehileMapActivity2 extends BaseActivity2 implements LocationSource, AMapLocationListener,
        OnGeocodeSearchListener, RouteSearch.OnRouteSearchListener,OnClickListener
{
    private int m_button;
    private int num;
    private AMap aMap;
    private MapView mMapView = null;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    private Marker regeoMarker;

    private String mCurrentCityName = "武汉";

    //private LatLonPoint mStartPoint = new LatLonPoint(30.6101657506,114.3058255885);//起点，设计院
    //private LatLonPoint mEndPoint = new LatLonPoint(30.6333656425,114.2755252507);//终点，116.481288,39.995576

    private LatLonPoint mStartPoint = new LatLonPoint(30.6333656425,114.2755252507);//起点
    private LatLonPoint mEndPoint = new LatLonPoint(30.6101657506,114.3058255885);//终点，设计院
    private LatLonPoint mStartPoint_bus = new LatLonPoint(40.818311, 111.670801);//起点，111.670801,40.818311
    private LatLonPoint mEndPoint_bus = new LatLonPoint(44.433942, 125.184449);//终点，

    //定位需要的声明
    private AMapLocationClient mLocationClient = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private OnLocationChangedListener mListener = null;//定位监听器

    private LatLonPoint latLonPoint = new LatLonPoint(39.90865, 116.39751);

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private double jing;
    private double wei;
    private String v_no;
    private String desc;
    private String addressName;
    private TextView v_address;
    private TextView v_address2;
    private Button BtnRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);
        //setHeadView(R.drawable.onclick_back_btn, "", "上车地点", 0, "", this, this, this);

        mContext = this.getApplicationContext();
        jing = getIntent().getDoubleExtra("jing", 114.17);
        wei = getIntent().getDoubleExtra("wei", 30.35);
        v_no=getIntent().getStringExtra("v_no");
        desc=getIntent().getStringExtra("desc");

        v_address2=(TextView) findViewById(R.id.v_address2);

        v_address=(TextView) findViewById(R.id.v_address);
        v_address.setText(desc);

        android.util.Log.i("cjwsjy", "------desc="+desc+"-------");

        setHeadView(R.drawable.onclick_back_btn, "", v_no, 0, "", this, this, this);

        m_button = 0;
        num = 0;
        //按钮
        BtnRoute = (Button)findViewById(R.id.button_amap);
        BtnRoute.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                onClickButton();
            }
        });

        //返回按钮
        //findViewById(R.id.search_img).setOnClickListener(this);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);

        //初始化地图变量
        if( aMap!=null ) return;

        //获取地图对象
        aMap = mMapView.getMap();

        //LatLng marker1 = new LatLng(30.35 , 114.17);
        LatLng marker1 = new LatLng( 30.610148,114.306091 );

        //设置地图的中心点
        //aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));

        //将地图的缩放级别调整到12级
        //aMap.moveCamera(CameraUpdateFactory.zoomTo(12));

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);
        //显示比例尺控件
        //settings.setScaleControlsEnabled(true);//显示比例尺控件

        //定位的小图标 默认是蓝点 这里自定义一团火，其实就是一张图片
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.index_06));
//        myLocationStyle.radiusFillColor(android.R.color.transparent);
//        myLocationStyle.strokeColor(android.R.color.transparent);
//        aMap.setMyLocationStyle(myLocationStyle);

        //开始定位
        initLoc();

        latLonPoint.setLatitude(wei);
        latLonPoint.setLongitude(jing);

        getAddress(latLonPoint);
    }

    private int  checkSelfPermission()
    {
        return 1;
    }

    private void onClickButton()
    {
        if(m_button!=0) return;

        android.util.Log.i("cjwsjy", "------onClickButton-------");

        mStartPoint.setLatitude(wei);
        mStartPoint.setLongitude(jing);

        //初始化 RouteSearch 对象
        mRouteSearch = new RouteSearch(this);

        //设置数据回调监听器
        mRouteSearch.setRouteSearchListener(this);

        searchRouteResult(2, RouteSearch.DrivingDefault);

        m_button++;
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode)
    {
        android.util.Log.i("cjwsjy", "------routeType="+routeType+"----mode="+mode+"---searchRouteResult");

        if (mStartPoint == null)
        {
            //ToastUtil.show(mContext, "起点未设置");
            android.util.Log.i("cjwsjy", "------error----1---searchRouteResult");
            return;
        }
        if (mEndPoint == null)
        {
            //ToastUtil.show(mContext, "终点未设置");
            android.util.Log.i("cjwsjy", "------error----2---searchRouteResult");
        }
        //showProgressDialog();

        //final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo( mStartPoint,mEndPoint  );
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo( mEndPoint,mStartPoint );
        if (routeType == 1)
        {
            // 公交路径规划
            // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,mCurrentCityName, 0);
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        }
        else if (routeType == 2)
        {
            android.util.Log.i("cjwsjy", "------ROUTE_TYPE_DRIVE-------searchRouteResult");
            // 驾车路径规划
            // 第一个参数表示路径规划的起点和终点，
            // 第二个参数表示驾车模式，
            // 第三个参数表示途经点，
            // 第四个参数表示避让区域，
            // 第五个参数表示避让道路
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
        else if (routeType == 3)
        {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
        else if (routeType == 4)
        {
            RouteSearch.FromAndTo fromAndTo_bus = new RouteSearch.FromAndTo(mStartPoint_bus, mEndPoint_bus);
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo_bus, mode,
                    "呼和浩特市", 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
//            query.setCityd("农安县");
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        }
    }

    //启动定位
    private void initLoc()
    {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode)
    {
        //dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000)
        {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null)
            {
                if (driveRouteResult.getPaths().size() > 0)
                {
                    mDriveRouteResult = driveRouteResult;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    //mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();

                    String des = AMapUtil.getFriendlyLength(dis)+"("+AMapUtil.getFriendlyTime(dur)+")";

                    android.util.Log.i("cjwsjy", "------time="+AMapUtil.getFriendlyTime(dur)+"-------");
                    android.util.Log.i("cjwsjy", "------km="+AMapUtil.getFriendlyLength(dis)+"-------");

                    //v_address.setText(des);

                    v_address2.setText(addressName+"   "+des);
                }
                else if (driveRouteResult != null && driveRouteResult.getPaths() == null)
                {
                    //ToastUtil.show(mContext, R.string.no_result);
                }

            }
            else
            {
                //ToastUtil.show(mContext, R.string.no_result);
            }
        }
        else
        {
            //ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation)
    {
        if (amapLocation != null)
        {
            if (amapLocation.getErrorCode() == 0)
            {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码

                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                //if( isFirstLoc==true )
                {
                    double Lati = amapLocation.getLatitude();
                    double Longi = amapLocation.getLongitude();
                    //LatLng marker1 = new LatLng( amapLocation.getLatitude(), amapLocation.getLongitude() );
                    //LatLng marker1 = new LatLng( 30.633325, 114.317822 );

                    //android.util.Log.i("cjwsjy", "------wei="+wei+"---jing="+jing+"------");

                    //设置缩放级别
                    //aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                    //将地图移动到定位点
                    //aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));

                    mEndPoint.setLatitude(Lati);
                    mEndPoint.setLongitude(Longi);

                    //Lati = wei;
                    //Longi = jing;

                    amapLocation.setLatitude(wei);
                    amapLocation.setLongitude(jing);
                    //点击定位按钮 能够将地图的中心移动到定位点
                    // 显示系统小蓝点
                    //mListener.onLocationChanged(amapLocation);

                    //添加图钉
                    regeoMarker = aMap.addMarker(getMarkerOptions(amapLocation));

                    //设置缩放级别
                    //aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + ""  + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
                    //Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();

                    isFirstLoc = false;
                    //mLocationClient.stopLocation();
                    //android.util.Log.i("cjwsjy", "--------buffer="+buffer.toString()+"---------");

                    num++;
                    //v_address.setText("Lati="+Lati+"--"+num+"--Longi="+Longi);

                    onClickButton();
                }
            }
            else
            {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                android.util.Log.i("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation)
    {
        //设置图钉选项
        MarkerOptions options = new MarkerOptions();
        //图标
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.index_22));
        //位置
        options.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
        StringBuffer buffer = new StringBuffer();
        buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() +  "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        //标题
        options.title(buffer.toString());
        //子标题
        options.snippet("这里好火");
        //设置多少帧刷新一次图片资源
        options.period(60);

        return options;
    }

    //定位
    private void getAddress(final LatLonPoint latLonPoint)
    {
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode)
    {
        //dismissDialog();
        if (rCode == 1000)
        {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null)
            {
                LatLng latlong1 = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                addressName = result.getRegeocodeAddress().getFormatAddress() + "附近";
                //aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong1, 15));
                //regeoMarker.setPosition(latlong1);
                v_address2.setText(addressName);

                Toast.makeText( VehileMapActivity2.this, addressName, Toast.LENGTH_LONG).show();
                android.util.Log.i("cjwsjy", "--------addressName="+addressName+"---------");
            }
            else
            {
                //ToastUtil.show(ReGeocoderActivity.this, R.string.no_result);
                Toast.makeText( VehileMapActivity2.this, "对不起，没有搜索到相关数据！", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            //ToastUtil.showerror(this, rCode);
            Toast.makeText( VehileMapActivity2.this, rCode, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.leftImage:
                finish();
                break;
            case R.id.rightImage:
                Log.d("cjwsjy", "------selectVehicleActivity-------");
                break;
        }
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener listener)
    {
        android.util.Log.i("cjwsjy", "------激活定位-------");
        mListener = listener;
    }

    //停止定位
    @Override
    public void deactivate()
    {
        android.util.Log.i("cjwsjy", "------停止定位-------");
        mListener = null;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

        //销毁定位客户端
        if(mLocationClient!=null)
        {
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
	
}
