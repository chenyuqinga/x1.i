package com.fotile.music.presenter;

import com.fotile.common.base.BasePresenter;
import com.fotile.common.base.BaseView;
import com.fotile.music.model.view.MusicsSearchView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;

import java.util.HashMap;
import java.util.Map;

import rx.subscriptions.CompositeSubscription;

/**
 * 文件名称：MusicSearchActivity
 * 创建时间：17-8-17 下午7:08
 * 文件作者：zhangqiang
 * 功能描述：Music搜索的Presenter
 */
public class MusicSearchPresenter implements BasePresenter {

    private static final String TAG = "MusicSearchPresenter";

    private static final String MUSIC_CATEGORY = "2";

    /**
     * 搜索为获得数据为空
     */
    public static final String DATA_NULL = "1";
    public static final String ILLEGAL_ERROR = "100";

    private MusicsSearchView musicsSearchView;

    @Override
    public void onCreate(CompositeSubscription compositeSubscription) {
    }

    @Override
    public void attachView(BaseView baseView) {
        musicsSearchView = (MusicsSearchView) baseView;
    }


    public void getMusicSearch(String type, int pageId) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, type);
        map.put(DTransferConstants.CATEGORY_ID, MUSIC_CATEGORY);
        map.put(DTransferConstants.PAGE, "" + pageId);

        CommonRequest.getSearchedTracks(map, new IDataCallBack<SearchTrackList>() {
            @Override
            public void onSuccess(SearchTrackList searchTrackList) {
                if (searchTrackList.getTracks().size() > 0) {
                    musicsSearchView.onSuccess(searchTrackList);
                } else {
                    musicsSearchView.onError(DATA_NULL);
                }

            }

            @Override
            public void onError(int i, String s) {
                musicsSearchView.onError(i+"");
            }
        });
    }
}
