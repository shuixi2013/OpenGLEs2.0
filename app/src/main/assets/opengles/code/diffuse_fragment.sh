precision mediump float; 
varying vec4 vColor;                                      // 接收从顶点着色器传过来的易变变量
varying vec4 vDiffuse; 
uniform vec4 uAmbient;

void main() { 
   gl_FragColor = vColor * uAmbient * vDiffuse;           // 给片源附上颜色值
}