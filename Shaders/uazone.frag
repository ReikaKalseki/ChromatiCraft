#import color
#import noise
#import effects

uniform float skyBright;
uniform float headYaw;
uniform float headPitch;

void main() 
{
	vec4 orig = texture2D(bgl_RenderedTexture, texcoord);
	
	vec3 sobel = getGrayscaledBlurredEdgeDetectedColor(texcoord);
	float dT = float(time)*0.01;
	vec2 pos = vec2(screenWidth, screenHeight)*texcoord*vec2(0.002, 0.0008);
	float n1 = snoise4d(vec4(pos.x, dT, pos.y, (headYaw+headPitch)*0.02));
	float n2 = snoise4d(vec4(pos.x, dT-2487.47193, pos.y, (headYaw+headPitch)*0.017));
	float noiseVal = min(1.0, 4.0*max(0.0, 0.15-abs(min(n1, n2))));
	float ex = 2.5+skyBright*1.5;
	sobel.r = pow(sobel.r, ex);
	sobel.g = pow(sobel.g, ex);
	sobel.b = pow(sobel.b, ex);
	vec3 add = sobel.rgb*vec3(0.608, 0.757, 0.227)*(10.0-skyBright*7.5)*intensity*noiseVal;
	vec3 net = orig.rgb+add;
    
    gl_FragColor = vec4(min(1.0, net.x), min(1.0, net.y), min(1.0, net.z), orig.a);
}