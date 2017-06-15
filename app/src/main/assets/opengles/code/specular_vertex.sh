uniform mat4 uMVPMatrix;                                   // 总变换矩阵
uniform mat4 uMMatrix;                                     // 变换矩阵，如平移，缩放，旋转
uniform vec3 uLightPosition;                               // 光源位置
uniform vec3 uCamera;                                      // 相机位置
attribute vec3 aPosition;                                  // 顶点位置
attribute vec4 aColor;                                     // 顶点颜色
attribute vec3 aNormal;                                    // 顶点法向量
varying vec3 vPosition;                                    // 用于传递给片元着色器的易变变亮
varying vec4 vColor;                                       // 用于传递给片元着色器的易变变量
varying vec4 vSpecular;                                    // 镜面反射易变变量

void lightDiffuse(in vec3 normal, inout vec4 specular,     // 输入法向量，输出计算后的散射光
   in vec3 lightPosition, in vec4 lightSpecular) {         // 输入光源位置，输入漫射光强度
   vec3 tempNormal = aPosition + normal;                   // 计算变换后的法向量
   vec3 newNormal = (uMMatrix * vec4(tempNormal, 1)).xyz - (uMMatrix * vec4(aPosition, 1)).xyz;
   newNormal = normalize(newNormal);                       // 对法向量规格化
   vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
   vec3 vp = normalize(lightPosition - (uMMatrix * vec4(aPosition, 1)).xyz);     // 计算表面点到光源位置的向量vp
   vec3 halfVector=normalize(vp+eye);
   float shininess=50.0;
   float nDotViewHalfVector=dot(newNormal,halfVector);
   float powerFactor = max(0.0, pow(nDotViewHalfVector, shininess));        // 求法向量和vp向量的点积　与0的最大值
   specular = lightSpecular*powerFactor;                                    // 计算散射光的最终结果
}

void main() {
   gl_Position = uMVPMatrix * vec4(aPosition,1);           // 根据总变换矩阵计算此次绘制顶点的位置
   vPosition = aPosition;                                  // 将接收的顶点位置传递给片元着色器
   vColor = aColor;                                        // 将接收的顶点颜色传递给片元着色器
   vec4 targetSpecular = vec4(0, 0, 0, 0);
   lightDiffuse(normalize(aNormal), targetSpecular, uLightPosition, vec4(0.7, 0.7, 0.7, 1));
   vSpecular = targetSpecular;
}