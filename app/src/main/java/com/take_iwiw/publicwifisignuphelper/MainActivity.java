package com.take_iwiw.publicwifisignuphelper;

import android.content.Intent;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SimpleAdapter adapter;
    private List<Map<String, String>> list;
    private static final String LIST_VIEW_TEXT_NAME = "name";
    private static final String LIST_VIEW_TEXT_PARAM = "param";

    private static final String INVALID = "---";
    private String macAddress = INVALID;
    private String ssid = INVALID;
    private String dnsAddress1 = INVALID;
    private String dnsAddress2 = INVALID;
    private String gatewayAddress = INVALID;
    private String ipAddress = INVALID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set adapter for list view
        list = new ArrayList<Map<String, String>>();
        adapter = new SimpleAdapter(
                this,
                list,
                android.R.layout.simple_list_item_2,
                new String[] {LIST_VIEW_TEXT_NAME, LIST_VIEW_TEXT_PARAM},
                new int[] {android.R.id.text1, android.R.id.text2}
        );
        ListView listView = (ListView)findViewById(R.id.listViewConfig);
        listView.setAdapter(adapter);

        showIpConfig();
    }

    private String convertAddress(int ipAddress) {
        return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    private boolean getIpConfig() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo dhcpInfo  =wifiManager.getDhcpInfo();

            macAddress = wifiInfo.getMacAddress();
            ssid = wifiInfo.getSSID();
            dnsAddress1 = convertAddress(dhcpInfo.dns1);
            dnsAddress2 = convertAddress(dhcpInfo.dns2);
            gatewayAddress = convertAddress(dhcpInfo.gateway);
            ipAddress = convertAddress(dhcpInfo.ipAddress);

        } catch (Exception ex) {
            Log.e("MyApp", ex.getMessage());
            macAddress = INVALID;
            ssid = INVALID;
            dnsAddress1 = INVALID;
            dnsAddress2 = INVALID;
            gatewayAddress = INVALID;
            ipAddress = INVALID;
            return false;
        }

        showIpConfig();
        return true;
    }

    private void showIpConfig() {
        list.clear();

        Map<String, String> mapSSID = new HashMap<String, String>();
        mapSSID.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_ssid));
        mapSSID.put(LIST_VIEW_TEXT_PARAM, ssid);
        list.add(mapSSID);

        Map<String, String> mapIpAddress = new HashMap<String, String>();
        mapIpAddress.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_ip_address));
        mapIpAddress.put(LIST_VIEW_TEXT_PARAM, ipAddress);
        list.add(mapIpAddress);

        Map<String, String> mapGateway = new HashMap<String, String>();
        mapGateway.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_gateway));
        mapGateway.put(LIST_VIEW_TEXT_PARAM, gatewayAddress);
        list.add(mapGateway);

        Map<String, String> mapDns1 = new HashMap<String, String>();
        mapDns1.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_dns1));
        mapDns1.put(LIST_VIEW_TEXT_PARAM, dnsAddress1);
        list.add(mapDns1);

        Map<String, String> mapDns2 = new HashMap<String, String>();
        mapDns2.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_dns2));
        mapDns2.put(LIST_VIEW_TEXT_PARAM, dnsAddress2);
        list.add(mapDns2);

        Map<String, String> mapMacAddress = new HashMap<String, String>();
        mapMacAddress.put(LIST_VIEW_TEXT_NAME, getString(R.string.item_name_mac_address));
        mapMacAddress.put(LIST_VIEW_TEXT_PARAM, macAddress);
        list.add(mapMacAddress);

        adapter.notifyDataSetChanged();
    }

    public void onClickSignUp(View view) {
        if(!getIpConfig() || gatewayAddress.equals("0.0.0.0")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dlg_error_title))
                    .setMessage(getString(R.string.dlg_error_msg))
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Uri uri = Uri.parse("http://" + gatewayAddress);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public void onClickGetIpConfig(View view) {
        if(!getIpConfig()) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dlg_error_title))
                    .setMessage(getString(R.string.dlg_error_msg))
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        // todo
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        // todo
    }
}
