precision mediump float;

varying vec2 vTexture;              // 这里白天夜晚所使用的纹理坐标一致

varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;

uniform sampler2D aTextureDay;      // 地球白天纹理
uniform sampler2D aTextureNight;    // 地球夜晚纹理

void main() {
    vec4 finalDayColor;
    vec4 finalNightColor;

    finalDayColor = texture2D(aTextureDay, vTexture);
    finalDayColor = finalDayColor * vAmbient + finalDayColor * vDiffuse + finalDayColor * vSpecular;

    finalNightColor = texture2D(aTextureNight, vTexture);
    finalNightColor = finalNightColor * vec4(0.5, 0.5, 0.5, 1);

    if(vDiffuse.x > 0.21) {
       gl_FragColor = finalDayColor;
    } else if(vDiffuse.x < 0.05) {
       gl_FragColor = finalNightColor;
    } else {
       float t = (vDiffuse.x - 0.05) / 0.161121666;
       gl_FragColor = t * finalDayColor + (1.0 - t) * finalNightColor;      // 这里不能将1.0变为１
    }
}