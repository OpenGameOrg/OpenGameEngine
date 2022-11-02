$input v_normal, v_texcoord0, v_position

#include "../common/common.sh"

SAMPLER2D(s_texColor,  0);
void main()
{
    vec3 normal = normalize(v_normal);
    vec3 lightPos = vec3(1.0, 0.0, 0.0);
    vec3 lightDirection = normalize(lightPos - v_position);
    vec4 lightColor = vec4(1.0, 1.0, 1.0, 1.0);

    float diffuse = max(dot(normal, lightDirection), 0);

    vec4 color = toLinear(texture2D(s_texColor, v_texcoord0));
    gl_FragColor = color * mul(lightColor, diffuse);
}