/**
 * Created by DAM on 18/1/17.
 */
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class RestSynchronous {
    //creamos un atributo de tipo retrofit
    private static Retrofit retrofit;

    public static void main(String[] args) throws IOException {
        //le pasamos la url
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AtletaService atletaService = retrofit.create(AtletaService.class);

        /*creamos la llamada a la función de obtener todos los atletas
        Call<List<Athlete>> callAllAthletes = athleteService.getAllAthlete();
        Obtenemos la respuesta a la llamada (ejecutamos una respuesta)
        Response<List<Athlete>> response = callAllAthletes.execute();
        Simplificado: */
        //Bloquea a la espera de que el servidor conteste

        Response<List<Atleta>> responseAllAtleta = atletaService.getAllAtletas().execute();
        if (responseAllAtleta.isSuccessful()) {
            List<Atleta> AtletaList = responseAllAtleta.body();
            System.out.println("Status code: " + responseAllAtleta.code() + System.lineSeparator() +
                    "GET all athletes: " + AtletaList);
        } else { //si no obtiene una respuesta satisfactoria
            System.out.println("Status code: " + responseAllAtleta.code() +
                    "Message error: " + responseAllAtleta.errorBody());
        }


        //creamos la llamada a una opcion de url que sabemos que dará error ya que no existe
        Response<List<Atleta>> responseUrlError = atletaService.getError().execute();
        //como sabemos que no será satisfactoria solo creamos el mensaje de insatisfactorio
        if (!responseUrlError.isSuccessful()) {
            System.out.println("Status code: " + responseUrlError.code() + " Message error: " + responseUrlError.raw());
        }


        //Post
        Atleta atleta = new Atleta();
        atleta.setNombre("Michael");
        atleta.setApellidos("Jordan");
        atleta.setNacionalidad("EE.UU");
        atleta.setFechanacimiento(LocalDate.of(1994, 05, 16));
        Response<Atleta> postAtletas = atletaService.createAtleta(atleta).execute();

        if (postAtletas.isSuccessful()) {
            Atleta atletaResp = postAtletas.body();
            System.out.println("Status code: " + postAtletas.code() + System.lineSeparator() +
                    "POST player: " + atletaResp);

            //creamos una llamada para llamar a un atleta en concreto
            Response<Atleta> responseOneAtleta = atletaService.getAtleta(atletaResp.getId()).execute();
            if (responseOneAtleta.isSuccessful()) {
                System.out.println("GET ONE->Status code: " + responseOneAtleta.code() + " Athlete: " + responseOneAtleta.body());
            } else {
                System.out.println("Status code: " + responseOneAtleta.code() + "Message error: " + responseOneAtleta.errorBody());
            }

            //Put
            atletaResp.setNacionalidad("MICASA");
            Response<Atleta> putAtleta = atletaService.updateAtleta(atletaResp).execute();

            if (responseOneAtleta.isSuccessful()) {
                System.out.println("Status code: " + putAtleta.code() + System.lineSeparator() +
                        "PUT player: " + putAtleta.body());
            } else {
                System.out.println("Status code: " + putAtleta.code() + "Message error: " + putAtleta.errorBody());
            }

            //eliminar athleta
            Response<Void> atletaDelete = atletaService.deleteAtleta(atletaResp.getId()).execute();

            System.out.println("DELETE status code: " + atletaDelete.code());

            //Comprobamos que se ha borrado
            responseAllAtleta = atletaService.getAllAtletas().execute();

            System.out.println("Comprobación del delete " +
                    "Status code: " + responseAllAtleta.code() + System.lineSeparator() +
                    "GET players: " + responseAllAtleta.body());

        }
    }

}