#include <jni.h>
#include <stdio.h>
#include "edu_ysu_itrace_trackers_TobiiTracker.h"

 /* Class:     edu_ysu_itrace_trackers_TobiiTracker
 * Method:    jniConnectTobiiTracker
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_edu_ysu_itrace_trackers_TobiiTracker_jniConnectTobiiTracker
  (JNIEnv *env, jobject myObj, jint myInt) {
  	// printf("Called connect!\n");
  	return 0;
  }

/*
 * Class:     edu_ysu_itrace_trackers_TobiiTracker
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_ysu_itrace_trackers_TobiiTracker_close
  (JNIEnv *env, jobject myObj) {

  	// printf("Called close!\n");
  }

/*
 * Class:     edu_ysu_itrace_trackers_TobiiTracker
 * Method:    startTracking
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_ysu_itrace_trackers_TobiiTracker_startTracking
  (JNIEnv *env, jobject myObj) {

  	// printf("Called START!\n");
  }

/*
 * Class:     edu_ysu_itrace_trackers_TobiiTracker
 * Method:    stopTracking
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_ysu_itrace_trackers_TobiiTracker_stopTracking
  (JNIEnv *env, jobject myObj) {
  	// printf("Called START!\n");
  }
