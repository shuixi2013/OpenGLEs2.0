precision mediump float;

varying vec4 vColor;        // 接收从顶点着色器传过来的易变变量
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying float uClipDist;

void main() {
   if(uClipDist < 0.0)
       discard;
   gl_FragColor = vColor * vAmbient + vColor * vDiffuse + vColor * vSpecular;   // 给片源附上颜色值
}