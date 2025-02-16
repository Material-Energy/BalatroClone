varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform vec2 tex_size;
uniform vec2 tex_center;
uniform float timer;
uniform float seed;

vec4 output_pixl(vec2 pixelCoord){
    float shif = .1;
    pixelCoord = vec2(pixelCoord.x - .5f, pixelCoord.y);
    pixelCoord = vec2(pixelCoord.x / 1.5f, pixelCoord.y);

    float dis = mod(
        distance(pixelCoord, tex_center) - timer
    , shif) / shif;

    dis = dis * .5 + .25;

    if (dis < .7)
    return vec4(.4, .4, 0.95, 1.0);
    else
    return vec4(.2, .2, 1.0, 1.0);

    vec4 col = vec4(dis, dis, 1., 1.);
    return col;
}

void main() {
    vec4 col = texture2D(texture, vertTexCoord.st);

    vec4 out_col = output_pixl(vertTexCoord.st);

    gl_FragColor = vec4(
        ((col + out_col) / 2.).rgb,
        col.a
    );
}