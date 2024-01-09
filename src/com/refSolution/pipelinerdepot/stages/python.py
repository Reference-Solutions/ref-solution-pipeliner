import os
import requests
client_id = 'tech-client-03'
client_secret = 'xMTjYq7Prp2vIETEHYZ4eG6bOUIXIOBD'
accessTokenUrl = 'https://p2.authz.bosch.com/auth/realms/EU_RB_FLEATEST/protocol/openid-connect/token'

def get_access_token():
    proxies_value = {'http' : 'http://rb-proxy-de.bosch.com:8080' , 'https' : 'http://rb-proxy-de.bosch.com:8080' }
    response = requests.post(url = accessTokenUrl, data = {"grant_type": 'client_credentials'}, auth = (client_id, client_secret) , proxies=proxies_value)
    return response.json()["access_token"]


token = get_access_token()
print(token)