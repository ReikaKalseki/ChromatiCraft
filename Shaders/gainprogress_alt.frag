#import math
#import color
#import noise

void main() {
	vec2 texUV = texcoord;
	vec4 color = texture2D(bgl_RenderedTexture, texUV);
	float br = getVisualBrightness(color.rgb);
	float sc = 2.5;//1.5;//5.0*intensity;//2.5;
	float cd = snoise3d(vec3(texUV*sc*vec2(float(screenWidth)/float(screenHeight), 1.0), float(time)*0.5));
	float hue = 0.15*(1.0+cd)+0.42*(1.0-intensity);
	vec3 new = hsb2rgb(vec3(hue, 0.75, br));
	float f = intensity <= 1.0 ? intensity : (1.0625-intensity)*16.0;
	vec3 res = mix(color.rgb, new, f);
    gl_FragColor = vec4(res.x, res.y, res.z, color.a);
}