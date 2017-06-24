precision mediump float;

varying vec2 vTexture;              // 这里白天夜晚所使用的纹理坐标一致

varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;

uniform sampler2D aTextureDay;      // 月球纹理

void main() {
   vec4 finalDayColor = texture2D(aTextureDay, vTexture);
   gl_FragColor = finalDayColor * vAmbient + finalDayColor * vDiffuse + finalDayColor * vSpecular;
}