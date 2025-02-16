varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform vec2 tex_size;
uniform vec2 tex_center;
uniform float time;
uniform float seed;

vec2 square_uv(vec2 coord, vec2 res) {
    if (res.x >= res.y)
    coord.x = (coord.x + (res.y - res.x) / 2.0) * res.x / res.y;
    else
    coord.y = (coord.y + (res.x - res.y) / 2.0) * res.y / res.x;
    return coord / res;
}

const vec3 gradientColor = vec3(167. / 255., 85. / 255., 1.) * 0.6;
float Hash(in float p, in float scale) {
    p = mod(p, scale);
    return fract(sin(dot(vec2(p), vec2(27.16898, 38.90563))) * 5151.5473453);
}

float noise(in float p, in float scale ) {
    float f;
    p *= scale;

    f = fract(p);
    p = floor(p);

    f = f*f*(3.0-2.0*f);

    float res = mix(mix(Hash(p, scale),
                        Hash(p + 1.0, scale), f),
                    mix(Hash(p, scale),
                        Hash(p + 1.0, scale), f), f);
    return res;
}

float fbm(in float p, in float scale) {
    float f = 0.0;

    p = mod(p, scale);
    float amp = 0.7;

    for (int i = 0; i < 5; i++) {
        f += noise(p, scale) * amp;
        amp *= .5;
        scale *= 2.;
    }
    return min(f, 1.0);
}

const float slope = 2.;
vec3 enchantmentGlint(vec2 uv, float time) {
    vec3 color = gradientColor * max(fbm(uv.x - time, 8.), 0.2);
    color += gradientColor * max(fbm(uv.y - ((uv.x + (time * slope)) / slope), 8.) - 0.5, 0.);
    return color;
}

vec4 output_pixl(vec2 pixelCoord){
    vec3 col = enchantmentGlint(pixelCoord, time);
    return vec4(col, texture2D(texture, pixelCoord.xy).a);
}

void main() {
    vec2 err_sup = seed + tex_size + tex_center + time;

    vec4 col = texture2D(texture, vertTexCoord.st);

    vec4 out_col = output_pixl(vertTexCoord.st);

    gl_FragColor = vec4(
    ((col + out_col) / 2.).rgb,
    col.a
    );
}