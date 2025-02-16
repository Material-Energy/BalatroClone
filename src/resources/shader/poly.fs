varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform vec2 tex_size;
uniform vec2 tex_center;
uniform float time;
uniform float seed;

float hue(float s, float t, float h)
{
    float hs = mod(h, 1.)*6.;
    if (hs < 1.) return (t-s) * hs + s;
    if (hs < 3.) return t;
    if (hs < 4.) return (t-s) * (4.-hs) + s;
    return s;
}

vec4 RGB(vec4 c)
{
    if (c.y < 0.0001)
    return vec4(vec3(c.z), c.a);

    float t = (c.z < .5) ? c.y*c.z + c.z : -c.y*c.z + (c.y+c.z);
    float s = 2.0 * c.z - t;
    return vec4(hue(s,t,c.x + 1./3.), hue(s,t,c.x), hue(s,t,c.x - 1./3.), c.w);
}

vec4 HSL(vec4 c)
{
    float low = min(c.r, min(c.g, c.b));
    float high = max(c.r, max(c.g, c.b));
    float delta = high - low;
    float sum = high+low;

    vec4 hsl = vec4(.0, .0, .5 * sum, c.a);
    if (delta == .0)
    return hsl;

    hsl.y = (hsl.z < .5) ? delta / sum : delta / (2.0 - sum);

    if (high == c.r)
    hsl.x = (c.g - c.b) / delta;
    else if (high == c.g)
    hsl.x = (c.b - c.r) / delta + 2.0;
    else
    hsl.x = (c.r - c.g) / delta + 4.0;

    hsl.x = mod(hsl.x / 6., 1.);
    return hsl;
}


vec4 output_pixl(vec4 colour, vec2 pixelCoord) {

    vec4 hsv = HSL(colour);

    if (seed > 2.)
    pixelCoord = vec2(pixelCoord.x, pixelCoord.y);
    else
    pixelCoord = vec2(-pixelCoord.x, -pixelCoord.y);

    float h = mod((pixelCoord.x + pixelCoord.y) * 2. + time, 360.);
    hsv = vec4(h, max(hsv.g, .5), max(hsv.b, .5), colour.a);
    return RGB(hsv);
}

void main() {
    vec2 err_sup = seed + tex_size + tex_center + time;

    vec4 col = texture2D(texture, vertTexCoord.st);

    vec4 out_col = output_pixl(col, vertTexCoord.st);

    gl_FragColor = vec4(out_col.rgb, col.a);
}



