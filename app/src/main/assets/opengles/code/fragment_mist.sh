precision mediump float;

varying float vFogFactor;
varying vec2 vTexture;              // 这里白天夜晚所使用的纹理坐标一致

varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;

uniform sampler2D aTextureDay;      // 月球纹理

void main() {
   vec4 finalDayColor = texture2D(aTextureDay, vTexture);
   finalDayColor = finalDayColor * vAmbient + finalDayColor * vDiffuse + finalDayColor * vSpecular;

   vec4 fogColor = vec4(0.97,0.76,0.83,1.0);    // 雾的颜色
   if(vFogFactor != 0.0){                       // 如果雾因子为0，不必计算光照
       gl_FragColor = finalDayColor * vFogFactor + fogColor * (1.0 - vFogFactor);   // 物体颜色和雾颜色插值计算最终颜色
   } else {
       gl_FragColor = fogColor;
   }
}