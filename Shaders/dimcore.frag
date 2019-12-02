void main() {
    vec4 color = texture2D(bgl_RenderedTexture, texcoord);
    
    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}