package org.techtown.home.sns;

import org.techtown.home.sns.SnsPostInfo;

public interface OnSnsPostListener {
    void onDelete(SnsPostInfo snspostInfo);
    void onModify();
}
