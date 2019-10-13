/**
 * 用于绑定 Native类 和 Java类 的辅助头文件
 */

#define GET(T, name) jclass clazz = env->GetObjectClass(thiz); \
     jfieldID fid = env->GetFieldID(clazz, "handler", "J");  \
     T *name = (T *)env->GetLongField(thiz, fid);

#define SET(T) {T *t = new T(); \
    jclass clazz = env->GetObjectClass(thiz); \
    jfieldID fid = env->GetFieldID(clazz, "handler", "J"); \
    env->SetLongField(thiz, fid, (jlong)t);}

#define CLEAR(T) {jclass clazz = env->GetObjectClass(thiz); \
     jfieldID fid = env->GetFieldID(clazz, "handler", "J");  \
     T *object = (T *) env->GetLongField(thiz, fid); \
     if(object != NULL) delete object; \
     env->SetLongField(thiz, fid, (jlong)0);}
