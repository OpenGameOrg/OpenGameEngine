$input v_normal, v_position

#include "../common/common.sh"

uniform vec4 u_color;
uniform vec4 u_camPos;
uniform vec4 u_diffuseColor;
uniform vec4 u_specColor;
uniform vec4 u_ambienceColor;

void main()
{
    vec3 normal = normalize(v_normal);
    vec3 lightPos = vec3(1.0, 0.0, 0.0);
    vec3 lightDirection = normalize(lightPos - v_position);
    vec4 lightColor = vec4(1.0, 1.0, 1.0, 1.0);

    float diffuse = max(dot(normal, lightDirection), 0);

    vec3 viewDir = normalize(v_position - u_camPos.xyz);
    vec3 reflectionDir = reflect(lightDirection, normal);
    float specAmount = pow(max(dot(viewDir, reflectionDir), 0), 8);

    gl_FragColor = u_color * (diffuse * u_diffuseColor + specAmount * u_specColor + u_ambienceColor);
}