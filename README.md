# Jaffa
private void getFriends() {
        GraphRequest request= GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                // TODO Auto-generated method stub
                try
                {
                    Log.d("Garph Friend ", objects.toString(2));
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, installed");
        request.setParameters(parameters);
        request.executeAsync();

    }




loginButton.setReadPermissions("public_profile", "user_friends");
