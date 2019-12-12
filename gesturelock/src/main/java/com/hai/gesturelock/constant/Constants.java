package com.hai.gesturelock.constant;

public interface Constants {

	//*/ 手势密码点的状态
	int POINT_STATE_NORMAL = 0; // 正常状态

	int POINT_STATE_SELECTED = 1; // 按下状态

	int POINT_STATE_WRONG = 2; // 错误状态
	//*/

	String SHARED_GESTURE_LOCK_PSW_KEY = "shared_gesture_lock_psw_key";  //手势锁密码
	//是否开启指纹解锁
	String SHARED_FINGERPRINT_LOCK_KEY = "key_finger_scanner";
}
