uniform mat4 uMVPMatrix;       // 总变换矩阵
uniform mat4 uMMatrix;         // 变换矩阵，如平移，缩放，旋转
uniform vec3 uCamera;          // 相机位置
uniform vec3 uLightPosition;   // 光源位置

uniform vec4 uAmbient;         // 环境光强度
uniform vec4 uDiffuse;         // 漫射光强度
uniform vec4 uSpecular;        // 反射光强度

attribute vec2 aTexture;       // 纹理顶点
attribute vec3 aPosition;      // 顶点位置
attribute vec3 aNormal;        // 顶点法向量

varying float vFogFactor;      // 雾化因子
varying vec2 vTexture;         // 纹理顶点易变变量
varying vec4 vAmbient;         // 环境光易变变量
varying vec4 vDiffuse;         // 漫射光易变变量
varying vec4 vSpecular;        // 反射光易变变量

// 输入法向量，输出计算后的环境光、散射光、反射光强度，输入光源位置、输入环境光、漫射光、反射光强度
void pointLight(in vec3 normal, inout vec4 outAmbient, inout vec4 outDiffuse, inout vec4 outSpecular,
   in vec3 lightPosition, in vec4 inAmbient, in vec4 inDiffuse, in vec4 inSpecular) {
    outAmbient = inAmbient;                                                     // 直接输出环境光强度

    vec3 normalTarget = aPosition + normal;                                     // 计算变换后的法向量
    vec3 newNormal = (uMMatrix * vec4(normalTarget, 1)).xyz - (uMMatrix * vec4(aPosition, 1)).xyz;
    newNormal = normalize(newNormal);                                           // 对法向量规格化
    vec3 eye = normalize(uCamera - (uMMatrix * vec4(aPosition, 1)).xyz);        // 计算从表面点到摄像机的向量

    vec3 vp = normalize(lightPosition - (uMMatrix * vec4(aPosition, 1)).xyz);    // 计算从表面点到光源的向量
    //vec3 vp = normalize(lightPosition);

    vp = normalize(vp);
    vec3 halfVector = normalize(vp + eye);                                      // 求视线与光线的半向量
    float shininess = 50.0;                                                     // 粗糙度，越小越光滑
    float nDotViewPosition = max(0.0, dot(newNormal, vp));                      // 求法向量与vp的点积　与0的最大值
    outDiffuse = inDiffuse * nDotViewPosition;                                  // 计算散射光的最终强度

    float nDotViewHalfVector = dot(newNormal, halfVector);                      // 法向量与半向量的点积
    float powerFactor = max(0.0, pow(nDotViewHalfVector, shininess));           // 镜面发射光强度因子
    outSpecular = inSpecular * powerFactor;                                     // 计算最终反射光强度
}

// 计算雾因子的方法
float computeFogFactor(){
   float tmpFactor;
   float fogDistance = length(uCamera-(uMMatrix * vec4(aPosition, 1)).xyz);     // 顶点到摄像机的距离
   const float end = 450.0;                                                     // 雾结束位置
   const float start = 350.0;                                                   // 雾开始位置
   tmpFactor = max(min((end - fogDistance) / (end - start), 1.0), 0.0);         // 用雾公式计算雾因子
   return tmpFactor;
}

void main() {
    gl_Position = uMVPMatrix * vec4(aPosition, 1);          // 根据总变换矩阵计算此次绘制顶点的位置

    vec4 ambientTemp, diffuseTemp, specularTemp;

    // 计算定位光各通道强度   test aAmbient, aDiffuse, aSpecular: vec4(0.15,0.15,0.15,1.0), vec4(0.8,0.8,0.8,1.0), vec4(0.7,0.7,0.7,1.0)
    pointLight(normalize(aNormal), ambientTemp, diffuseTemp, specularTemp,
        uLightPosition, uAmbient, uDiffuse, uSpecular);

    vAmbient = ambientTemp;
    vDiffuse = diffuseTemp;
    vSpecular = specularTemp;

    vTexture = aTexture;                                    // 将接收的顶点颜色传递给片元着色器

    vFogFactor = computeFogFactor();
}