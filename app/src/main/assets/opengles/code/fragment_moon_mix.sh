precision mediump float;

varying vec2 vTexture;

varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;

uniform sampler2D aTextureDay;      // 球纹理

void main() {
   vec4 finalDayColor = texture2D(aTextureDay, vTexture);
   finalDayColor.a = (finalDayColor.r + finalDayColor.g + finalDayColor.b) / 3.0;
   gl_FragColor = finalDayColor * vAmbient + finalDayColor * vDiffuse + finalDayColor * vSpecular;
}