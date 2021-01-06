#import math
#import noise

uniform int hazeRed;
uniform int hazeGreen;
uniform int hazeBlue;

void main() {
    vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
	
	//float dx = min(texcoord.x, 1.0-texcoord.x)*float(screenWidth)/float(screenHeight);
	//float dy = min(texcoord.y, 1.0-texcoord.y)*float(screenHeight*0.0+1.0);
	//float d = min(dx, dy);
	float dx = (texcoord.x-0.5)*2.0;//*float(screenWidth)/float(screenHeight);
	float dy = (texcoord.y-0.5)*2.0;
	float d = 1.0-min(1.0, pow(dx*dx*dx*dx*dx*dx*dx*dx+dy*dy*dy*dy*dy*dy*dy*dy, 0.125));
	float df = 1+0.25*snoise(texcoord*150.0*vec2(float(screenWidth)/float(screenHeight), 1.0));
	float f = max(0.0, intensity-(d*0.018*1000.0*0.4+0.125)*df);
		
	vec2 pix = vec2(texcoord.x, texcoord.y);
	pix.x = roundToNearest(pix.x, 4.0/(float(screenWidth)));
	pix.y = roundToNearest(pix.y, 4.0/(float(screenHeight)));
		
	float t = float(time)+5000.0;
	float tx = float(t)*0.3;
	float ty = float(t)*0.3;
	//tx *= (pix.x-0.5)*0.72;
	//ty *= (pix.y-0.5)*0.72;
	//tx *= -(pix.y-0.5);
	//ty *= -(pix.x-0.5);
	
	float sx = 2*(0.5-pix.x);
	float sy = 2*(0.5-pix.y);
	tx *= sx;
	ty *= sy;
	
	/*
	if (texcoord.x > 0.5) {
		tx *= -1;
	}
	if (texcoord.y > 0.5) {
		ty *= -1;
	}*/
	
	vec2 pix2 = vec2(texcoord.x, texcoord.y);
	float sc = min(8.0, 1.0/max(0.01, intensity));
	pix2.x = roundToNearest(pix2.x, sc/(float(screenWidth)));
	pix2.y = roundToNearest(pix2.y, sc/(float(screenHeight)));
	
	float ns = snoise(pix2*vec2(screenWidth, screenHeight)*0.09+vec2(tx, ty)*0.4);
	f *= 0.5+0.5*min(1.0, ns*ns*ns*3.0);
	f = min(max(f, 0.0), 1.0);
	
	float r = float(hazeRed)/255.0*f;
	float g = float(hazeGreen)/255.0*f;
	float b = float(hazeBlue)/255.0*f;
	vec3 net = vec3(min(1.0, r+orig.r), min(1.0, g+orig.g), min(1.0, b+orig.b));
    
    gl_FragColor = vec4(net.x, net.y, net.z, orig.a);
}

